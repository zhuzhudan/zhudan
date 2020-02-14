package server.core;

import java.util.List;

/**
 * 整个Catalina Servlet容器以及组件
 * 子标签有：Listener、GlobalNamingResources、Service
 */
public class Server {
    private List<Service> services;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
