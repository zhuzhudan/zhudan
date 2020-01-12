package com.springframework.beans.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinition {
    private String factoryBeanName;
    private Object beanClass;
    private String beanName;
    private String beanClassName;

    private String[] annotationType;
    private Object[] annotationClass;


    private Map<String, Object> propertyMap = new HashMap<>();
    private Map<String, Object> constrcutorArgs = new HashMap<>();

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public Object getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Object beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String[] getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String[] annotationType) {
        this.annotationType = annotationType;
    }

    public Object[] getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Object[] annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, Object> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public Map<String, Object> getConstrcutorArgs() {
        return constrcutorArgs;
    }

    public void setConstrcutorArgs(Map<String, Object> constrcutorArgs) {
        this.constrcutorArgs = constrcutorArgs;
    }

    /**
     * 获得类信息
     * @return
     */
    public Class<?> resolveBeanClass(){
        String className = getBeanClassName();
        Class<?> resolvedClass = null;
        try {
            resolvedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.beanClass = resolvedClass;
        return resolvedClass;
    }
}
