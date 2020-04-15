package com.study.service.impl;

import com.study.service.HelloService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HelloServiceImpl implements HelloService {
    @Override
    public String methodA() {
        try {
            TimeUnit.MILLISECONDS.sleep(getRandom(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "method A run";
    }

    @Override
    public String methodB() {
        try {
            TimeUnit.MILLISECONDS.sleep(getRandom(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "method B run";
    }

    @Override
    public String methodC() {
        try {
            TimeUnit.MILLISECONDS.sleep(getRandom(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "method C run";
    }

    // 取随机数
    private int getRandom(int maxInteger){
        Random random = new Random();
        return random.nextInt(maxInteger + 1);
    }
}
