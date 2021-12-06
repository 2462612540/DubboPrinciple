package com.zhuangxiaoyan;

import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class UpdateBeanPostProcessors implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.contains("ServiceBean")) {
            //这里设置id，否则会造成同一个Service有多个group时，只能注入第一个service
            ServiceBean serviceBean = (ServiceBean) bean;
            serviceBean.setId(beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
