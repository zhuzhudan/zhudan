package server.core;

import server.Servlet;

/**
 * 表示一个Servlet
 */
public class Wrapper {
    private Servlet instance;

    public Servlet getInstance() {
        return instance;
    }

    public void setInstance(Servlet instance) {
        this.instance = instance;
    }
}
