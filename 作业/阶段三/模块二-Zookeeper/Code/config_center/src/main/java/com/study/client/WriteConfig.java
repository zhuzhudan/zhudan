package com.study.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 初始化zookeeper，设置mysql信息，进行修改节点
 */
public class WriteConfig {
    public static void main(String[] args) {
        // 配置文件的路径
        String path = "/mysql";

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181").sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("config").build();
        curatorFramework.start();

        // 初始化
        try {
            if (curatorFramework.checkExists().forPath(path) == null){
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(path);
            }

            curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(path+"/url", "jdbc:mysql://localhost:3306/zdy_mybatis?serverTimezone=UTC".getBytes());

            curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(path+"/username", "root".getBytes());

            curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(path+"/password", "12345678".getBytes());

            curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(path+"/driver", "com.mysql.cj.jdbc.Driver".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 更新
        try {
            // curatorFramework.setData().forPath(path+"/url", "jdbc:mysql://localhost:3306/jpa?serverTimezone=UTC".getBytes());
            // curatorFramework.setData().forPath(path+"/url", "jdbc:mysql://localhost:3306/zdy_mybatis?serverTimezone=UTC".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
