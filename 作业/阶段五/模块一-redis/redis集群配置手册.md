## 1. 下载、编译redis

1、下载：

```
[root@localhost ~]# wget http://download.redis.io/releases/redis-5.0.9.tar.gz
```

2、解压：

```
[root@localhost ~]# tar xzf redis-5.0.9.tar.gz 
```

3、安装 gcc 环境：

``` 
[root@localhost ~]# yum install gcc-c++
```

4、解压后，进入 redis-5.0.9 文件夹：

```
[root@localhost ~]# cd redis-5.0.9
```

5、编译：

```
[root@localhost redis-5.0.9]# make

编译出错：
...
In file included from adlist.c:34:0:
zmalloc.h:50:31: 致命错误：jemalloc/jemalloc.h：没有那个文件或目录
 #include <jemalloc/jemalloc.h>
...
```

重新编译：

``` 
[root@localhost redis-5.0.9]# make MALLOC=lib
```

6、进入 src 文件夹中，安装（每个实例的安装的文件夹不同）：

```
[root@localhost redis-5.0.9]# cd ./src
[root@localhost src]# make install
```



## 2. 搭建 redis 集群  ##

1、创建集群文件夹

```
[root@localhost ~]# mkdir redis-cluster
[root@localhost ~]# cd redis-cluster/
```

2、创建三主三从服务器实例（端口号：7001～7006），并修改 redis.conf 文件

```
[root@localhost redis-cluster]# mkdir master-7001
[root@localhost redis-cluster]# cd /root/redis-5.0.9/src/
[root@localhost src]# make install PREFIX=/root/redis-cluster/master-7001

[root@localhost src]# cd /root/redis-cluster/master-7001/
[root@localhost master-7001]# cd bin
```

复制 redis.conf 到创建的bin文件夹中

``` 
[root@localhost bin]# cp /root/redis-5.0.9/redis.conf /root/redis-cluster/master-7001/bin
```

修改 redis.conf 文件

```
[root@localhost bin]# vim redis.conf

...
# bind 127.0.0.1 #注释掉
...
protected-mode no #由yes改为no
...
port 7001 #由6379->改为7001
...
daemonize yes #由no改为yes
...
cluster-enabled yes #取消掉注释
...
```

复制7001，创建7002～7006实例，并修改为相应的端口号（port）

```
[root@localhost bin]# cd ../..
[root@localhost redis-cluster]# cp -r master-7001 master-7002
[root@localhost redis-cluster]# cp -r master-7001 master-7003
[root@localhost redis-cluster]# cp -r master-7001 slaver-7004
[root@localhost redis-cluster]# cp -r master-7001 slaver-7005
[root@localhost redis-cluster]# cp -r master-7001 slaver-7006
```

3、创建start.sh，启动所有的实例

```
[root@localhost redis-cluster]# vim start.sh

cd master-7001/bin
./redis-server redis.conf
cd ../..

cd master-7002/bin
./redis-server redis.conf
cd ../..

cd master-7003/bin
./redis-server redis.conf
cd ../..

cd slaver-7004/bin
./redis-server redis.conf
cd ../..

cd slaver-7005/bin
./redis-server redis.conf
cd ../..

cd slaver-7006/bin
./redis-server redis.conf
cd ../..

[root@localhost redis-cluster]# chmod u+x start.sh #(赋写和执行的权限)
[root@localhost redis-cluster]# ./start.sh
```

4、创建Redis集群

```
# cluster-replicas ： 1 1从机 前三个为主 
[root@localhost bin]# ./redis-cli --cluster create 192.168.1.111:7001 192.168.1.111:7002 192.168.1.111:7003 192.168.1.111:7004 192.168.1.111:7005 192.168.1.111:7006 --cluster-replicas 1
```

5、客户端连接集群

```
# -c 表示是以redis集群方式进行连接
[root@localhost bin]# ./redis-cli -h 192.168.1.111 -p 7001 -c 
```

查看集群状态

```
192.168.1.111:7001> cluster info
```

查看集群中的节点

```
192.168.1.111:7001> cluster nodes
```



## 3. 扩容

增加7007、7008一主一从两台服务器

1、创建7007实例

```
[root@localhost redis-cluster]# mkidr master-7007
[root@localhost redis-cluster]# cd /root/redis-5.0.9/src/

# 创建7007实例
[root@localhost src]# make install PREFIX=/root/redis-cluster/master-7007
[root@localhost src]# cd /root/redis-cluster/master-7007

[root@localhost master-7007]# cd bin

# 复制redis.conf到bin文件中
[root@localhost bin]# cp /root/redis-5.0.9/redis.conf /root/redis-cluster/master-7007/bin/
```

修改redis.conf文件，同上，注意修改不同的端口（port 7007）

2、创建7008实例

```
[root@localhost bin]# cd ../../
[root@localhost redis-cluster]# cp -r master-7007 slaver-7008
```

修改redis.conf文件，修改端口（port 7008）

3、启动7007实例

```
[root@localhost redis-cluster]# cd master-7007/bin
[root@localhost bin]# ./redis-server redis.conf
```

4、添加7007实例到集群中

```
[root@localhost bin]# cd /root/redis-cluster/master-7001/bin
[root@localhost bin]# ./redis-cli --cluster add-node 192.168.1.111:7007 192.168.1.111:7001
```

5、重新给节点分槽

```
[root@localhost bin]# ./redis-cli --cluster reshard 192.168.1.111:7007
```

6、输入要分配的槽数量

```
How many slots do you want to move (from 1 to 16384)? 3000
```

7、输入接收槽的结点id

```
What is the receiving node ID?
```

这里准备给7007分配槽，通过cluster nodes查看7007结点id为380a33e0653f73a1779c547cdfcb7b7442d605c2

8、输入源结点id

```
Please enter all the source node IDs. 
  Type 'all' to use all the nodes as source nodes for the hash slots. 
  Type 'done' once you entered all the source nodes IDs.
```

Source node #1: all

9、启动7008实例

```
[root@localhost redis-cluster]# cd /root/redis-cluster/slaver-7008/bin
[root@localhost bin]# ./redis-server redis.conf
```

10、添加7008从结点，将7008作为7007的从结点

```
[root@localhost bin]# cd /root/redis-cluster/master-7001/bin
[root@localhost bin]# ./redis-cli --cluster add-node 192.168.1.111:7008 192.168.1.111:7007 --cluster-slave --cluster-master-id 380a33e0653f73a1779c547cdfcb7b7442d605c2(主节点7007的id)
```

