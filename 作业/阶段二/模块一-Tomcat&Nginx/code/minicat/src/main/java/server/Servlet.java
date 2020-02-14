package server;

/**
 * Servlet规范
 */
public interface Servlet {
    void init() throws Exception;

    void destroy() throws Exception;

    void service(Request request, Response response) throws Exception;
}
