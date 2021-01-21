package com.github;


import com.github.proxy.RpcClientProxy;
import com.github.remote.client.NettyClient;
import com.github.remote.dto.ClientRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ClientMain {

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        IProductService service = (IProductService) proxy.getProxy(IProductService.class);
        Product product = service.get(1L);
        System.out.println(product);
    }
}
