package com.springframework.beans.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC容器的实例化的默认实现
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    private volatile List<String> beanDefinitionNames = new ArrayList<String>(256);

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(256);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public List<String> getBeanDefinitionNames() {
        return beanDefinitionNames;
    }

    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    public int getBeanDefinitionMapCount() {
        return beanDefinitionMap.size();
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Map<String, Object> getEarlySingletonObjects() {
        return earlySingletonObjects;
    }

    // 根据BeanDefinition进行注册
    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanDefinition.getBeanName());
        if (existingDefinition != null) {
            this.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
        else {
            this.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
            this.beanDefinitionNames.add(beanDefinition.getBeanName());
        }

    }

    @Override
    public void preInstantiateSingletons() {
        // 所有bean的名字
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

        setBeanFactory(this);
        // 触发所有非延迟加载单例bean的初始化
        for (String beanName : beanNames) {
            // 实例化当前bean
            getBean(beanName);
        }
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects){
            this.singletonObjects.put(beanName, singletonObject);
            this.earlySingletonObjects.remove(beanName);
        }
    }

    @Override
    public void registerEarlySingleton(String beanName, Object earlySingletonObject) {
        this.earlySingletonObjects.put(beanName, earlySingletonObject);
    }

    public BeanDefinition getBeanDefinitionByBeanName(String beanName){
        return this.beanDefinitionMap.get(beanName);
    }

    public String getBeanDefinitionByClassName(String className){
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            String beanName = beanDefinitionEntry.getKey();

            if(beanDefinition.getBeanClassName().equals(className)){
                return beanName;
            }
        }
        return null;
    }

}
