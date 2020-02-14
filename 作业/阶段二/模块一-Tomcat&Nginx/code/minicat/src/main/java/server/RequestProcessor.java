package server;

import server.core.Context;
import server.core.Mapper;
import server.core.Wrapper;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread {
    private Socket socket;
    private Map<String, HttpServlet> servletMap;
    private Mapper mapper;

    public RequestProcessor(Socket socket, Mapper mapper){
        this.socket = socket;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try {
            // 获取输入流，从输入流中读取请求信息
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 从Request中获取请求路径
            String hostName = request.getHost();            // localhost
            String contextName = request.getContextUrl();   // /demo1
            String path = request.getUrl();                 // /study

            // 根据Host、Context、url查找对应的Wrapper（Servlet），如果没有就是静态资源或请求错误
            if (mapper.isExistsWrapper(hostName, contextName, path)){
                // 动态资源servlet请求
                Wrapper wrapper = mapper.getWrapper(hostName, contextName, path);
                HttpServlet httpServlet = (HttpServlet) wrapper.getInstance();
                httpServlet.service(request, response);
            } else {
                // 静态资源处理
                // 如果是静态资源需要查找Context的真实地址，查找path对应的文件
                Context context = mapper.getContext(hostName, contextName);
                String absoluteResourcePath = "";
                if (context != null){
                    // 获取Context的绝对路径，即项目地址
                    absoluteResourcePath = context.getServletMappings().get(contextName);
                    // 文件的绝对路径 = 项目地址 + 文件路径
                    absoluteResourcePath += path;
                }
                response.outputHtml(absoluteResourcePath);
            }

            socket.close();
            System.out.println("");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
