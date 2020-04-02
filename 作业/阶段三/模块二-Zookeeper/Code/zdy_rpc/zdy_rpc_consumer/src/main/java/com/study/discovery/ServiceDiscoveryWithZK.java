package com.study.discovery;

import com.study.codec.RpcDecoder;
import com.study.codec.RpcEncoder;
import com.study.handler.RpcProxyHandler;
import com.study.netty.NettyClient;
import com.study.protocol.RpcRequest;
import com.study.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDiscoveryWithZK implements ServiceDiscovery {
    private String CONNECTION_STR = "127.0.0.1:2181";

    // 服务地址的本地缓存
    List<String> serviceRepos = new ArrayList<>();

    // 服务地址与客户端创建连接
    Map<String, NettyClient> serviceClients = new ConcurrentHashMap<>();

    CuratorFramework curatorFramework = null;
    {
        // 初始化zookeeper的连接，会话超时时间50s，衰减重试
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
    public NettyClient discovery(String serviceName) {
        // 服务地址查找
        // registry/com.study.service.UserService
        String path = "/" + serviceName;

        try {
            serviceRepos = curatorFramework.getChildren().forPath(path);

            for (String serviceRepo : serviceRepos) {
                createConnect(serviceRepo);
            }

            registryWatch(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balanceStrategy(path);
    }

    /**
     * 与服务端创建连接
     */
    private void createConnect(String serviceRepo) {
        if (!serviceClients.containsKey(serviceRepo)) {
            String[] urls = serviceRepo.split(":");
            NettyClient nettyClient = new NettyClient(urls[0], Integer.parseInt(urls[1]));
            nettyClient.connect();
            serviceClients.put(serviceRepo, nettyClient);
        }
    }

    /**
     * 进行负载均衡算法
     * @param path
     * @return
     */
    private NettyClient balanceStrategy(String path){
        // 如果只有1台服务器，不用负载均衡，直接返回
        if (serviceRepos.size() == 1){
            return serviceClients.get(serviceRepos.get(0));
        }
        List<Integer> times = new ArrayList<>();

        long min = 0;
        long current = System.currentTimeMillis();

        int i = 0;
        for (String serviceRepo : serviceRepos) {
            try {
                Stat stat = new Stat();
                byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath(path + "/" + serviceRepo);
                long time = 0;
                // 取出zk节点的修改时间，如果未超过5s，获取上次响应时间
                if(current - stat.getMtime() < 5000) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    byteBuffer.put(bytes, 0, bytes.length);
                    byteBuffer.flip();
                    time = byteBuffer.getLong();
                }

                // 获取所有服务器中响应时间最小的一台，将其下标存入
                if (min > time){
                    times.clear();
                    times.add(i);
                    min = time;
                } else if (min == time){
                    times.add(i);
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 如果有多台服务器的响应时间一样，随机返回一台
        if (times.size() > 1){
            int length = times.size();
            Random random = new Random();
            return serviceClients.get(serviceRepos.get(times.get(random.nextInt(length))));

        }
        // 否则返回响应时间最短的一台
        return serviceClients.get(serviceRepos.get(times.get(0)));
    }

    /**
     * 设置监听
     * @param path
     * @throws Exception
     */
    private void registryWatch(final String path) throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener nodeCacheListener = (curatorFramework1, pathChildrenCacheEvent) -> {
            ChildData data = pathChildrenCacheEvent.getData();
            String tempPath = "";
            String url = "";
            switch (pathChildrenCacheEvent.getType()){
                case CHILD_ADDED:
                    tempPath = data.getPath();
                    url = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                    createConnect(url);
                    System.out.println("监听到：子节点增加: "+url);
                    break;
                case CHILD_REMOVED:
                    tempPath = data.getPath();
                    url = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                    NettyClient nettyClient = serviceClients.get(url);
                    nettyClient.closeClient();
                    serviceClients.remove(url);
                    System.out.println("监听到：子节点删除: "+url);
                    break;
                default:
                    break;
            }
            // 再次更新本地的缓存地址
            serviceRepos = curatorFramework1.getChildren().forPath(path);
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start(PathChildrenCache.StartMode.NORMAL);
    }
}
