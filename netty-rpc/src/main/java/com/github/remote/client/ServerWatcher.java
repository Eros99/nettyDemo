package com.github.remote.client;

import com.github.zk.ZookeeperFactory;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;


public class ServerWatcher implements Watcher {
    @SneakyThrows
    @Override
    public void process(WatchedEvent event) {
        CuratorFramework curator = ZookeeperFactory.create();
        String path = event.getPath();
        curator.getChildren().usingWatcher(this);
        List<String> paths= curator.getChildren().forPath(path);
        for (String p : paths) {

        }
    }
}
