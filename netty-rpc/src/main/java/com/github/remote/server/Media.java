package com.github.remote.server;


import com.github.remote.dto.ClientRequest;
import com.github.remote.dto.ServerResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {

    public static Map<String, BeanMethod> beanMethodMap = new HashMap<>();

    private Media() {}

    public Object process(ClientRequest request) {
        String methodName = request.getMethodName();
        ServerResponse result = null;
        BeanMethod beanMethod = beanMethodMap.get(methodName);
        if (beanMethod != null) {
            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();
            Class<?>[] parameterType = method.getParameterTypes();
            Object content = request.getContent();
            try {
                result = (ServerResponse) method.invoke(bean, content);
                System.out.println(result.getResult());
                result.setId(request.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class Singleton {
        private static Media instance = new Media();
    }
    public static Media getInstance() {
        return Singleton.instance;
    }
}
