package com.springframework.beans;

/**
 * IOC的顶级接口，只定义规范
 */
public interface BeanFactory {
    /**
     * 根据beanName从IOC容器中获取一个实例bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);

    Object getBean(Class clazz);
}
