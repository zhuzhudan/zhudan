package com.study.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 从zookeeper动态获取数据库配置
 */
public class ZookeeperConfigCenter {
    private String CONNECTION_STR = "127.0.0.1:2181";

    private CuratorFramework curatorFramework;

    // 配置文件的路径
    private String PATH = "/mysql";

    // mysql配置
    private Properties properties = new Properties();

    public ZookeeperConfigCenter() {
        // 初始化zookeeper的连接，会话超时时间5s，衰减重试
        initZookeeperServer();

        // 从zookeeper拉取的数据库配置
        getConfigData();

        // 对节点进行监听，刷新配置
        registryWatch();
    }

    // 初始化zookeeper的连接，会话超时时间5s，衰减重试
    private void initZookeeperServer(){
        // 连接zk
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("config").build();
        curatorFramework.start();

        // 创建节点
        try {
            if (curatorFramework.checkExists().forPath(PATH) == null){
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从zookeeper拉取的数据库配置
    private void getConfigData(){
        try {
            List<String> list = curatorFramework.getChildren().forPath(PATH);
            for (String key : list) {
                byte[] bytes = curatorFramework.getData().forPath(PATH + "/" + key);
                String value = new String(bytes);
                if (value != null && value.length() > 0){
                    properties.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 对节点进行监听，刷新配置
    private void registryWatch(){
        PathChildrenCache cache = new PathChildrenCache(curatorFramework, PATH, true);
        PathChildrenCacheListener listener = (curatorFramework1, pathChildrenCacheEvent) -> {
            ChildData data = pathChildrenCacheEvent.getData();
            switch (pathChildrenCacheEvent.getType()){
                case CHILD_UPDATED:
                    getConfigData();

                    // 关闭旧连接
                    DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringContextUtil.getBean("dataSource");
                    dynamicDataSource.getConnection().close();

                    // 创建新的连接池
                    ZookeeperConfigCenter configCenter = new ZookeeperConfigCenter();
                    Properties properties = configCenter.getProperties();
                    DruidDataSource dataSource = new DruidConfig().initDataSource(properties);

                    // 设置新的数据源
                    Map<Object, Object> targetDataSources = new HashMap<>();
                    targetDataSources.put("first", dataSource);
                    dynamicDataSource.setTargetDataSources(targetDataSources);
                    dynamicDataSource.setDefaultTargetDataSource(dataSource);
                    dynamicDataSource.afterPropertiesSet();

                    DynamicDataSource.setDataSource("first");
                    break;

                default:
                    break;
            }
        };
        cache.getListenable().addListener(listener);
        try {
            cache.start(PathChildrenCache.StartMode.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
