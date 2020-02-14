package server.core;

/**
 * 创建链接器实例，用于创建服务端Socket并进行监听，以等待客户端请求链接
 * 一个Service可以有多个Connector组件，绑定到一个Container中
 */
public class Connector {
    // 端口号
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
