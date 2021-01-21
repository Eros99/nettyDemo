package com.github;


public interface IProductService {

    void save(Product product);

    Product get(Long id);
}
