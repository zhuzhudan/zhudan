package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import server.core.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    private Server server;

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {
        // 加载解析server.xml
        loadServer();

        // 定义线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue, threadFactory, handler);

        // 将每个Connector绑定到一个线程，因为每个Connector会监听不同的端口
        for (Service service : this.server.getServices()) {
            Mapper mapper = service.getMapper();
            for (Connector connector : service.getConnectors()) {
                Coyote coyote = new Coyote(connector, threadPoolExecutor, mapper);
                threadPoolExecutor.execute(coyote);
            }
        }
    }

    /**
     * 加载解析server.xml
     */
    private void loadServer(){
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();// <Server>

            // 先查询Server
            this.server = new Server();

            // 再查找Service，一个Server可以包含多个Service
            List<Element> serviceElementList = rootElement.selectNodes("Service");

            List<Service> serviceList = new ArrayList<>();
            for (Element serviceElement : serviceElementList) {
                Service service = new Service();

                List<Connector> connectors = new ArrayList<>();
                // 一个Service可以有多个Connector
                List<Element> connectorElementList = serviceElement.selectNodes("Connector");
                for (Element connectorElement : connectorElementList) {
                    Connector connector = new Connector();
                    String port = connectorElement.attributeValue("port");// 绑定的端口号
                    connector.setPort(Integer.parseInt(port));
                    connectors.add(connector);
                }
                service.setConnectors(connectors);

                // 一个Service只能有一个Engine
                Element engineElement = (Element) serviceElement.selectSingleNode("Engine");
                Engine engine = new Engine();
                service.setEngine(engine);

                // Host、Context、Wrapper组成Mapper
                Mapper mapper = new Mapper();
                service.setMapper(mapper);

                // 一个Engine有多个Host
                List<Element> hostElementList = engineElement.selectNodes("Host");
                for (Element hostElement : hostElementList) {
                    // <Host name="localhost" appBase="部署目录的绝对地址"></Host>
                    Host host = new Host();
                    String hostName = hostElement.attributeValue("name"); // localhost
                    host.setName(hostName);
                    String appBase = hostElement.attributeValue("appBase"); // 部署目录的绝对地址
                    host.setAppBase(appBase);

                    mapper.addHost(hostName, host);

                    // 每个Host可以有多个Context
                    List<Element> contextElementList = hostElement.selectNodes("Context");
                    for (Element contextElement : contextElementList) {
                        Context context = new Context();
                        // <Context docBase="/Users/yingdian/web_demo" path="/web3"></Context>
                        String docBase = contextElement.attributeValue("docBase"); // 部署目录的绝对地址
                        String path = contextElement.attributeValue("path"); // Web应用的Context路径
                        // Context的对应关系
                        context.getServletMappings().put(path, docBase);
                        mapper.addContext(hostName, path, context);
                    }

                    // ------以上是配置解析xml
                    // ------下面是根据host的目录位置扫描此host下的所有项目
                    File file = new File(appBase);
                    for (File appDirectory : file.listFiles()) {
                        if (appDirectory.isDirectory()) {
                            // 获得项目名 Context
                            String appPath = "/" + appDirectory.getName();
                            String appDirectoryPath = appDirectory.getPath();
                            Context context = new Context();
                            context.getServletMappings().put(appPath, appDirectoryPath);
                            mapper.addContext(hostName, appPath, context);

                            // 查找项目下/WEB-INF/web.xml，并解析加载
                            loadWrapper(hostName, appPath, appDirectoryPath, mapper);
                        }

                    }

                }
                serviceList.add(service);
            }
            this.server.setServices(serviceList);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过项目下的 项目/WEB-INF/web.xml，初始化并加载Wrapper
     * 约定web.xml位置结构都相同：项目地址/WEB-INF/web.xml
     */
    private void loadWrapper(String hostName, String contextPath, String appDirectoryPath, Mapper mapper) {
        File webxml = new File(appDirectoryPath + "/WEB-INF/web.xml");
        if (webxml.exists()){
            try {
                InputStream inputStream = new FileInputStream(webxml);
                SAXReader saxReader = new SAXReader();

                Document document = saxReader.read(inputStream);
                Element rootElement = document.getRootElement();

                // 先查询servlet，再去servlet-mapping中查找
                List<Element> selectNodes = rootElement.selectNodes("//servlet");
                for (Element element : selectNodes) {
                    // <servlet-name>study</servlet-name>
                    Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                    String servletName = servletNameElement.getStringValue();

                    // <servlet-class>server.StudyServletN</servlet-class>
                    Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                    String servletClass = servletClassElement.getStringValue();

                    // 根据servlet-name的值找到url-pattern
                    Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                    // <url-pattern>/study</url-pattern>
                    String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();

                    // 根据读取的Class反射创建Servlet，约定Servlet的目录结构都一样
                    Wrapper wrapper = new Wrapper();
                    // 通过类加载加载 项目/WEB-INF/classes 下的Class，并创建Servlet实例
                    URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(appDirectoryPath+"/WEB-INF/classes").toURI().toURL()});
                    Object instance = urlClassLoader.loadClass(servletClass).newInstance();
                    wrapper.setInstance((HttpServlet) instance);

                    // 设置Servlet和Url的映射关系
                    mapper.addWrapper(hostName, contextPath, urlPattern, wrapper);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Minicat程序启动入口
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
