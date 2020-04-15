package com.study.util;

/**
 * 将浏览器请求的requestIp与线程绑
 */
public class RequestHolder {
    private static ThreadLocal requestLocal;

    public static void setRequest(String request) {
        if (requestLocal == null)
            requestLocal = new ThreadLocal();
        requestLocal.set(request);
    }

    public static String getRequest() {
        if (requestLocal == null)
            return null;
        else
            return (String) requestLocal.get();
    }

}
