package com.github.remote.server;


import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Getter@Setter
public class BeanMethod {

    private Object bean;
    private Method method;


}
