第一、二题

预：如果zookeeper的连接不是本机，可修改zdy_rpc_consumer子模块下的com.study.discover包下ServiceDiscoveryWithZK中、zdy_rpc_provider、zdy_rpc_provider2子模块下com.study.registry包下RegistryCenterWithZK中，zookeeper的连接

1、启动zdy_rpc_provider、zdy_rpc_provider2子模块的ZdyRpcProviderApplication、ZdyRpcProvider2Application

2、启动zdy_rpc_consumer子模块ClientBootStrap的main方法

3、出现success之后，关闭其中一个服务端ZdyRpcProviderApplication或ZdyRpcProvider2Application

4、再出现success之后，可启动刚刚关闭的服务端



第三题说明：

预：新建两个数据库，一个库中创建一个表，数据表可用文件夹中提供的“配置文件-第三题”文件夹中的两个，数据表名都一致user，内容不一致，用于查看数据源变更后是否有变化

1、请先修改config_center项目中com.study.client包下的WriteConfig.java中相应的配置信息启动（注释为：初始化 部分）

2、运行WriteConfig中的main方法

3、注释掉初始化部分，修改注释为更新部分中的配置信息，放开中的一个

4、在浏览器中查看：http://localhost:9090/user/list

5、启动com.study.ConfigConterApplication，完全启动后，再运行WriteConfig中的main方法，WriteConfig运行结束后，再次刷新浏览器，查看变化