package com.github.remote.dto;


import lombok.Builder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {

    private final Long id;
    private Object content;
    private String methodName;
    private final AtomicLong atomic = new AtomicLong(1);

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ClientRequest() {
        id = atomic.incrementAndGet();
    }

    public Long getId() {
        return id;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
