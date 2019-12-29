package com.study.sqlSession;

import com.study.pojo.Configuration;
import com.study.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    /**
     *
     * @param configuration：封装着配置信息
     * @param mappedStatement：存放着sql配置信息
     * @return
     */
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

    public int update(Configuration configuration, MappedStatement mappedStatement, Object parameter) throws Exception;
}
