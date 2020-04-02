package com.study.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 初始化、配置数据源
 */
@Configuration
public class DruidConfig {

    @Bean
    public DataSource firstDataSource(){
        ZookeeperConfigCenter configCenter = new ZookeeperConfigCenter();
        Properties properties = configCenter.getProperties();

        DruidDataSource dataSource = initDataSource(properties);

        return dataSource;
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource firstDataSource){
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("first", firstDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

    public DruidDataSource initDataSource(Properties properties){
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();

        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));

        dataSource.setInitialSize(3);
        dataSource.setMinIdle(3);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);

        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(false);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        return dataSource;
    }

}
