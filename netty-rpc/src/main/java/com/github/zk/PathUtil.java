package com.github.zk;


import com.github.util.Constants;

import java.net.InetSocketAddress;

public class PathUtil {

    public static String path(String serviceName) {
        return Constants.NETTY_SERVER  + "/" + serviceName;
    }
}
