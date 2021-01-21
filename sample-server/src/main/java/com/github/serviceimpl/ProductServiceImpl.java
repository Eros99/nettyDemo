package com.github.serviceimpl;

import com.github.IProductService;
import com.github.Product;
import com.github.annotation.RpcClient;
import org.springframework.stereotype.Service;

@RpcClient
public class ProductServiceImpl implements IProductService {
    @Override
    public void save(Product product) {
        System.out.println("save ok");
    }

    @Override
    public Product get(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("1");
        return product;
    }
}
