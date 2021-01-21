package com.github.remote.client;


import com.github.remote.dto.ClientRequest;
import com.github.remote.dto.ServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class CompleteFuture implements Callable<ServerResponse> {

    public static ConcurrentHashMap<Long, CompleteFuture> commonFutures = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private ServerResponse response;
    private long startTime = System.currentTimeMillis();

    public CompleteFuture(ClientRequest request) {
        commonFutures.put(request.getId(), this);
    }

    public static void setResponse(ServerResponse response) {
        CompleteFuture completeFuture = commonFutures.get(response.getId());
        if (completeFuture != null) {
            Lock lock = completeFuture.lock;
            lock.lock();
            try {
                completeFuture.response = response;
                completeFuture.condition.signal();
                commonFutures.remove(response.getId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean done() {
        return response != null;
    }

    @Override
    public ServerResponse call() throws Exception {
        lock.lock();
        try {
            while(!done()) {
                condition.await();
            }
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            lock.unlock();
        }
    }

    public static class FutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> set = commonFutures.keySet();
            for (Long id : set) {
                if (commonFutures.get(id) == null) {
                    commonFutures.remove(id);
                }
            }
        }
    }

    static {
        FutureThread thread = new FutureThread();
        thread.setDaemon(true);
        thread.start();
    }
}
