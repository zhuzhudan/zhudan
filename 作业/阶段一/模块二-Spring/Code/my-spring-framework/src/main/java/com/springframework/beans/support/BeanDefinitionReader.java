package com.springframework.beans.support;

import com.springframework.context.support.AbstractApplicationContext;

/**
 * 定义读取配置信息的抽象类
 * 可通过xml或者annotation读取
 */
public abstract class BeanDefinitionReader {
    private final AbstractApplicationContext applicationContext;

    public BeanDefinitionReader(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过文件地址加载信息
     * @param locations
     * @return
     */
    public void loadBeanDefinitions(String... locations){
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    public AbstractApplicationContext getAbstractApplicationContext() {
        return applicationContext;
    }

    public abstract void loadBeanDefinitions(String location);

}
