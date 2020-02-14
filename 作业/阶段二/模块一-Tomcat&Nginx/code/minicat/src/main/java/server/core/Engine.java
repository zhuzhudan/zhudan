package server.core;

/**
 * 整个Servlet引擎，用来管理多个虚拟站点
 * 一个Service只能有一个Engine，但是一个Engine可以有多个Host
 */
public class Engine {
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
