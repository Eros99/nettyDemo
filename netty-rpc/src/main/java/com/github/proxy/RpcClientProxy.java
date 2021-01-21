package com.github.proxy;


import com.github.remote.client.NettyClient;
import com.github.remote.dto.ClientRequest;
import com.github.remote.dto.ServerResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private final NettyClient rpcRequest;

    public RpcClientProxy(NettyClient rpcRequest) {
        this.rpcRequest = rpcRequest;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClientRequest request = new ClientRequest();
        request.setMethodName(method.getName());
        request.setContent(args);
        return rpcRequest.send(request).getResult();
    }


    public Object getProxy(Class<?> type) {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, this);
    }
}
