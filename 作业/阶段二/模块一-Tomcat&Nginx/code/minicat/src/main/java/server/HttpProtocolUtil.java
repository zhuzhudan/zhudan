package server;

/**
 * http协议工具类，主要是提供响应头信息，现只提供200和404的情况
 */
public class HttpProtocolUtil {
    /**
     * 为响应码200提供响应头信息
     * @return
     */
    public static String getHttpHeader200(long contentLength){
                // ============响应头===============
        return "HTTP/1.1 200 OK \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + contentLength + " \n" +
                // ============空行================
                "\r\n";
                // ============响应体===============
    }

    /**
     * 为响应码404提供响应头信息（此处包含响应体（数据内容））
     * @return
     */
    public static String getHttpHeader404(){
        String str404 = "<h1>404 not found</h1>";

                // ============响应头===============
        return "HTTP/1.1 404 NOT Found \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + str404.getBytes().length + " \n" +
                // ============空行================
                "\r\n" +
                // ============响应体===============
                str404;
    }


}
