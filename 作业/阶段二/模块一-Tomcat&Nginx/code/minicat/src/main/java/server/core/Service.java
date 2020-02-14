package server.core;

import java.util.List;

/**
 * 是Server的内部组件，一个Server包含多个Service
 * Service子标签有：Listener、Executor、Connector、Engine
 */
public class Service {
    private Engine engine;
    private List<Connector> connectors;
    private Mapper mapper;

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public List<Connector> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
