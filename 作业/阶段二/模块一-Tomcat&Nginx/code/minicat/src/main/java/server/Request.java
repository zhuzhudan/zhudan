package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request对象（根据InputStream输入流封装）
 */
public class Request {
    private String method;  // 请求方式，比如 GET / POST
    private String url;     // 请求相对路径，或Servlet路径，例如 / ，/index.html
    private String host;    // 主机名称，例如 localhost:8080
    private String contextUrl;  // 项目地址，例如 /web_demo

    private InputStream inputStream;    // 输入流，其他属性从输入流中解析出来

    public Request() {
    }

    // 构造器，输入流传入
    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        // 从输入流中读取请求信息
        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);

        String inputStr = new String(bytes);
        // GET /web_demo/study HTTP/1.1
        // Host: localhost:8080
        // Connection: keep-alive
        // Cache-Control: max-age=0
        // Upgrade-Insecure-Requests: 1
        // User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36
        // Sec-Fetch-User: ?1
        // Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
        // Sec-Fetch-Site: none
        // Sec-Fetch-Mode: navigate
        // Accept-Encoding: gzip, deflate, br
        // Accept-Language: zh-CN,zh;q=0.9
        // Cookie: JSESSIONID=34C946439D0CF62AC2872D3568A86DE1; JSESSIONID=5C3DF7CDE3AA50C9502AE82650C2E720

        // 获取第一行请求头信息
        String firstLineStr = inputStr.split("\\n")[0]; // GET /demo1/study HTTP/1.1
        String[] strings = firstLineStr.split(" ");
        this.method = strings[0];   // GET
        String[] appUrls = strings[1].split("/");// /web_demo/study
        if (appUrls.length > 1){
            this.contextUrl = "/" + appUrls[1];
            this.url = strings[1].substring(this.contextUrl.length());
        }

        // 获取主机信息
        String secondLineStr = inputStr.split("\\n")[1]; // Host: localhost:8080
        String[] split = secondLineStr.split(": ");
        this.host = split[1].split(":")[0];

        System.out.println("===============>>> method: " + method);     // GET
        System.out.println("===============>>> host: " + host);         // localhost
        System.out.println("===============>>> appUrl: " + contextUrl); // /demo1
        System.out.println("===============>>> url: " + url);           // /study
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(String contextUrl) {
        this.contextUrl = contextUrl;
    }
}
