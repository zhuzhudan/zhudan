package com.study.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 注册中心
 */
public class RegistryCenterWithZK implements RegistryCenter {
    private String CONNECTION_STR = "127.0.0.1:2181";

    CuratorFramework curatorFramework = null;
    {
        // 初始化zookeeper的连接，会话超时时间5s，衰减重试
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("registry").build();
        curatorFramework.start();
    }

    @Override
    public void registry(String serviceName, String serviceAddress) {
        String servicePath = "/" + serviceName;
        try {
            // 判断节点是否存在
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            // serviceAddress: ip:port
            String addressPath = servicePath + "/" + serviceAddress;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);

            System.out.println("端口：8989，服务注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServerResponseTime(String timeData, String path){
        try {
            Stat stat = curatorFramework.setData().forPath(path, timeData.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
