package com.study.protocol;

/**
 * 将方法进行封装
 */
public class RpcRequest {
    // 请求对象的Id
    private String requestId;

    // 服务名，类名
    private String className;

    // 方法名，具体的逻辑
    private String methodName;

    // 形参类型列表
    private Class<?>[] parameterTypes;

    // 实参列表
    private Object[] parameters;

    //=============== get / set 方法 ================
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
