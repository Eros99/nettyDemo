package com.github.config;


import com.github.annotation.RpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
public class RpcScannerConfig implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        CustomerScanner scanner = CustomerScanner.getScanner((BeanDefinitionRegistry) beanFactory, RpcClient.class);
        String property = applicationContext.getEnvironment().getProperty("rpc.service");
        if (property == null) {
            property = this.getClass().getPackage().getName();
        }
        scanner.setResourceLoader(applicationContext);
        int scan = scanner.scan(property);
        log.info("service扫描的数量 [{}]", scan);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
