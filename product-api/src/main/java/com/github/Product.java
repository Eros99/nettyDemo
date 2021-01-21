package com.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Product {

    private Long id;
    private String name;
    private Double price;
}
