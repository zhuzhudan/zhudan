package com.study.config;

import com.study.pojo.Configuration;
import com.study.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");

        List<Element> list = rootElement.selectNodes("//insert|update|select|delete");//对应xml文件中一个个的select标签
        for (Element element : list) {
            //解析mapper.xml内容
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sqlTrim = element.getTextTrim();
            String commandType = element.getQName().getName();

            //封装mapper.xml
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sqlTrim);
            mappedStatement.setCommandType(commandType);

            //将mapper放入集合之中
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }


    }
}
