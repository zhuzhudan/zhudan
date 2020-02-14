package server.core;

import java.util.*;

/**
 * 建立url和host、context、servlet的映射关系
 */
public class Mapper {
    private Map<String, MappedHost> hosts = new HashMap<>();

    public Map<String, MappedHost> getHosts() {
        return hosts;
    }

    public void setHosts(Map<String, MappedHost> hosts) {
        this.hosts = hosts;
    }

    // 添加Host
    public void addHost(String name, Host host){
        MappedHost newHost = new MappedHost(name, host);
        hosts.put(host.getName(), newHost);
    }

    public boolean isExistsHost(String hostName){
        return hosts.containsKey(hostName);
    }

    // 添加Context
    public void addContext(String hostName, String path, Context context){
        MappedHost host = hosts.get(hostName);
        if (host != null) {
            MappedContext newContext = new MappedContext(path, context);
            host.contexts.put(path, newContext);
        }
    }

    public boolean isExistsContext(String hostName, String path){
        MappedHost host = hosts.get(hostName);
        if (host == null){
            return false;
        }
        return host.contexts.containsKey(path);
    }

    public Context getContext(String hostName, String contextPath){
        MappedHost host = hosts.get(hostName);
        if (host != null){
            MappedContext context = host.contexts.get(contextPath);
            if (context != null){
                return context.object;
            }
        }
        return null;
    }

    // 添加Servlet
    public void addWrapper(String hostName, String contextPath, String path, Wrapper wrapper){
        MappedHost host = hosts.get(hostName);
        if (host != null) {
            MappedContext context = host.contexts.get(contextPath);
            if (context != null){
                MappedWrapper newWrapper = new MappedWrapper(path, wrapper);
                context.wrappers.put(path, newWrapper);
            }
        }
    }

    public boolean isExistsWrapper(String hostName, String contextPath, String path){
        MappedHost host = hosts.get(hostName);
        if (host == null){
            return false;
        }
        MappedContext context = host.contexts.get(contextPath);
        if (context == null){
            return false;
        }
        return context.wrappers.containsKey(path);
    }

    // 根据映射关系获取Servlet
    public Wrapper getWrapper(String hostName, String contextPath, String path){
        MappedHost host = hosts.get(hostName);
        if (host != null){
            MappedContext context = host.contexts.get(contextPath);
            if (context != null){
                MappedWrapper mappedWrapper = context.wrappers.get(path);
                return mappedWrapper.object;
            }
        }
        return null;
    }

    // 静态内部类 基类
    protected abstract static class MapElement<T> {
        public final String name;
        public final T object;

        public MapElement(String name, T object) {
            this.name = name;
            this.object = object;
        }
    }

    // 一个MappedHost可以有多个MappedContext
    protected static final class MappedHost extends MapElement<Host> {
        public Map<String, MappedContext> contexts;

        public MappedHost(String name, Host host) {
            super(name, host);
            this.contexts = new HashMap<>();
        }
    }

    // 一个MappedContext可以有多个MappedWrapper
    protected static final class MappedContext extends MapElement<Context>{
        public Map<String, MappedWrapper> wrappers;

        public MappedContext(String name, Context context) {
            super(name, context);
            this.wrappers = new HashMap<>();
        }
    }

    protected static final class MappedWrapper extends MapElement<Wrapper> {
        public MappedWrapper(String name, Wrapper wrapper) {
            super(name, wrapper);
        }
    }
}
