package com.study.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

// 第二步：用于存放sqlMapConfig.xml中的配置信息，核心配置类
// 传递时，只需传递Configuration对象即可，因为即包含DataSource信息，也包含每个Statement信息
public class Configuration {

    private DataSource dataSource;// 使用dom4j解析后，放到dataSource中


    // Key: statementId（namespace.id）
    // Value：封装好的mappedStatement对象
    Map<String, MappedStatement> mappedStatementMap = new HashMap<String, MappedStatement>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
