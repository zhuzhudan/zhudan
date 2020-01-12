package com.springframework.beans;

public class BeanWrapper {
    public Object wrappedObject;

    public BeanWrapper(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedInstance() {
        return wrappedObject;
    }

    public Class<?> getWrappedClass(){
        return getWrappedInstance().getClass();
    }
}
