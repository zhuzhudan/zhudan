package com.study.pojo;

// 第二步：用于存放mapper.xml文件的sql语句，映射配置信息
// 在解析时会产生多个
public class MappedStatement {
    // id标识
    private String id;
    // 返回值类型
    private String resultType;
    // 参数值类型
    private String parameterType;
    // sql语句
    private String sql;

    private String commandType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
