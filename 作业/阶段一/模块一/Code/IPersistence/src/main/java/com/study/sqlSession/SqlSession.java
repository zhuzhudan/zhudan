package com.study.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     * @param statementId：对应xml文件中的namespace.id，sql的唯一标识，
     *                   从Configuration中的mappedStatementMap中找到相应的MappedStatement
     * @return
     */
    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    //根据条件查询单个
    public <T> T selectOne(String statementId, Object... params) throws Exception;


    // 为Dao接口生成代理实现类
    public <T> T getMapper(Class<?> mapperClass);

    public int insert(String statementId, Object params) throws Exception;

    public int update(String statementId, Object params) throws Exception;

    public int delete(String statementId, Object params) throws Exception;

}
