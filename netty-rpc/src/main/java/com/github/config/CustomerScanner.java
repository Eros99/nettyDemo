package com.github.config;


import com.github.annotation.RpcClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

public class CustomerScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annotationClass;

    private static Class<? extends Annotation> staticAnnotationClass = null;

    public CustomerScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    public static CustomerScanner getScanner(BeanDefinitionRegistry beanFactory, Class<RpcClient> rpcClientClass) {
        staticAnnotationClass = rpcClientClass;
        CustomerScanner scanner = new CustomerScanner(beanFactory);
        scanner.setAnnotationClass(rpcClientClass);
        return scanner;
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition)
                && beanDefinition.getMetadata().hasAnnotation(this.annotationClass.getName());
    }



    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(staticAnnotationClass));
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }
}
