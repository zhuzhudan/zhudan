package server.core;

/**
 * 代表一个虚拟主机/站点，可以配置多个虚拟主机
 * 一个Host可包含多个Context
 */
public class Host {
    private String name;
    private String appBase;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }
}
