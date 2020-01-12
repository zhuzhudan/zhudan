package com.study.io;

import java.io.InputStream;

// 第一步：家在配置文件
public class Resources {

    // 借助类加载器实现：根据配置文件的路径，将配置文件加载成字节输入流，存储在内存中
    public static InputStream getResourceAsStream(String path){
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }
}
