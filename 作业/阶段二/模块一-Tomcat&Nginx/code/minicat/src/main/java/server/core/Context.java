package server.core;

import java.util.HashMap;
import java.util.List;

/**
 * 表示一个web应用
 * 一个web应用包含多个Wrapper
 */
public class Context {
    private List<Wrapper> wrapperList;
    private HashMap<String, String> servletMappings = new HashMap<>();

    public List<Wrapper> getWrapperList() {
        return wrapperList;
    }

    public void setWrapperList(List<Wrapper> wrapperList) {
        this.wrapperList = wrapperList;
    }

    public HashMap<String, String> getServletMappings() {
        return servletMappings;
    }

    public void setServletMappings(HashMap<String, String> servletMappings) {
        this.servletMappings = servletMappings;
    }

}
