package com.github;

import com.github.annotation.Reference;
import com.github.remote.dto.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ProductController {

    @Reference
    private IProductService productService;


    public ServerResponse save(Product product) {
        productService.save(product);
        product.setId(2L);
        return ServerResponse.success(product);
    }

    @RequestMapping("/get")
    public Product saveList(Long id) {
        return productService.get(id);
    }
}
