package com.study.factory;

import com.study.utils.ConnectionUtils;

public class CreateBeanFactory {

    public static ConnectionUtils getInstanceStatic(){
        return new ConnectionUtils();
    }

    public ConnectionUtils getInstance(){
        return new ConnectionUtils();
    }
}
