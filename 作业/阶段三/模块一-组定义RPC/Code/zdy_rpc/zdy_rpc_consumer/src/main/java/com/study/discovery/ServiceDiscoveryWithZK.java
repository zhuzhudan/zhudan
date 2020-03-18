package com.study.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServiceDiscoveryWithZK implements ServiceDiscovery {
    private String CONNECTION_STR = "127.0.0.1:2181";

    // 服务地址的本地缓存
    List<String> serviceRepos = new ArrayList<>();

    CuratorFramework curatorFramework = null;
    {
        // 初始化zookeeper的连接，会话超时时间5s，衰减重试
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("registry").build();
        curatorFramework.start();
    }

    /**
     * 服务的查找，设置监听
     * @param serviceName
     * @return
     */
    @Override
    public String discovery(String serviceName) {
        // 服务地址查找
        // registry/com.study.service.UserService
        String path = "/" + serviceName;

        try {
            serviceRepos = curatorFramework.getChildren().forPath(path);
            registryWatch(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 针对已有的地址做负载均衡，随机算法
        int length = serviceRepos.size();
        Random random = new Random();

        return serviceRepos.get(random.nextInt(length));
    }

    /**
     * 设置监听
     * @param path
     * @throws Exception
     */
    private void registryWatch(final String path) throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener nodeCacheListener = (curatorFramework1, pathChildrenCacheEvent) -> {
            // 再次更新本地的缓存地址
            serviceRepos = curatorFramework1.getChildren().forPath(path);
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start(PathChildrenCache.StartMode.NORMAL);
    }
}
