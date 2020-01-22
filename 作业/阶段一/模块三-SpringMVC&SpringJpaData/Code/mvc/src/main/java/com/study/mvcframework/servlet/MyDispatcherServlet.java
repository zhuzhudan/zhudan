package com.study.mvcframework.servlet;

import com.study.mvcframework.annotations.*;
import com.study.mvcframework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    // 缓存扫描到的类的全限定类名
    private List<String> classNames = new ArrayList<>();

    // ioc容器
    private Map<String, Object> ioc = new HashMap<>();

    // handlerMapping
    // 存储url和Method之间的映射关系
    // private Map<String, Method> handlerMapping = new HashMap<>();
    private List<Handler> handlerMapping = new ArrayList<>();

    // 存储handler和权限之间的对应关系
    private Map<Handler, Set<String>> handlerSecuritiesMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理请求：根据url，找到对应的Method方法，进行调用
        // 获取url
        // String requestURI = req.getRequestURI();
        // Method method = handlerMapping.get(requestURI);// 获取到一个反射的方法

        // 反射调用，需要传入对象，需要传入参数，此处无法完成调用，没有把对象缓存起来，没有参数
        // method.invoke()

        // 根据uri获取到能够处理当前请求的handler（从handlerMapping中）
        Handler handler = getHandler(req);

        if(handler == null){
            resp.getWriter().write("404 not found");
            return;
        }

        // 参数绑定
        // 获取所有参数类型数组，这个数组的长度就是最后要传入的args数组的长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 根据上述数组长度创建一个新的数组（参数数组，是要传入反射调用的）
        Object[] paramValues = new Object[parameterTypes.length];

        // 以下就是为了向参数数组中赋值，而且还需保证参数的顺序和方法中形参顺序一致
        Map<String, String[]> parameterMap = req.getParameterMap();

        Set<String> usernames = handlerSecuritiesMapping.get(handler);
        // 如果usernames不是空，则需要进行权限验证
        if (usernames != null && !usernames.isEmpty()){
            String[] paramNames = parameterMap.get("username");
            if (paramNames == null){
                resp.getWriter().write("URL must have \"username\" parameter, like: /xxx/xx?username=xx");
                return;
            }
            for (String paramName : paramNames) {
                if(!usernames.contains(paramName)){
                    resp.getWriter().write("You do not have permission to access this page");
                    return;
                }
            }
        }

        // 遍历request中所有参数（填充除了request，response之外）
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            // name=1&name=2    name [1,2]
            String value = StringUtils.join(param.getValue(), ","); // 如同 1,2

            // 如果参数和方法中的参数匹配上了，填充数据
            if (!handler.getParamIndexMapping().containsKey(param.getKey())) continue;

            // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paramValues
            Integer index = handler.getParamIndexMapping().get(param.getKey());

            if (index > -1) {
                paramValues[index] = value; // 将前台传递过来的参数值填充到对应的位置去
            }
        }

        if(handler.getParamIndexMapping().containsKey(HttpServletRequest.class.getSimpleName())) {
            Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
            paramValues[requestIndex] = req;
        }
        if(handler.getParamIndexMapping().containsKey(HttpServletResponse.class.getSimpleName())) {
            Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
            paramValues[responseIndex] = resp;
        }

        // 最终调用handler的method属性
        try {
            handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) return null;

        String url = req.getRequestURI();
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()) continue;
            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1、加载配置文件 springmvc.properties
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2、扫描相关的类，扫描注解
        doScan(properties.getProperty("scanPackage"));

        // 3、初始化bean对象（实现ioc容器，基于注解）
        doInstance();

        // 4、实现依赖注入
        doAutowired();

        // 5、构造一个HandlerMapping处理器映射器，将配置好的url和method建立映射关系
        initHandlerMapping();

        System.out.println("my mvc 初始化完成");
        // 等待请求进入，处理请求

    }

    // 构造一个HandlerMapping处理器映射器（最关键）
    // 目的：将Method与url建立关联
    private void initHandlerMapping() {
        if(ioc.isEmpty()) return;

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取ioc中当前遍历的对象的class类型
            Class<?> aClass = entry.getValue().getClass();

            if(!aClass.isAnnotationPresent(MyController.class)) continue;

            String baseUrl = "";
            if(aClass.isAnnotationPresent(MyRequestMapping.class)){
                MyRequestMapping annotation = aClass.getAnnotation(MyRequestMapping.class);
                baseUrl = annotation.value(); // <==> /demo
            }

            Set<String> securities = new HashSet<>();
            if (aClass.isAnnotationPresent(Security.class)){
                Security annotation = aClass.getAnnotation(Security.class);
                for (String security : annotation.value()) {
                    securities.add(security);
                }
            }

            // 获取方法
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                // 如果方法没有标识@MyRequestMapping，就不处理
                if(!method.isAnnotationPresent(MyRequestMapping.class)) continue;

                // 如果有标识，才处理
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                String methodUrl = annotation.value(); // <==> /query
                String url = baseUrl + methodUrl; // 计算出来的url：/demo/query

                // 将method的所有信息及url封装为handler
                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(url));

                // 计算参数位置信息
                // query(HttpServletRequest request, HttpServletResponse response, String name)
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if(parameter.getType() == HttpServletRequest.class ||
                            parameter.getType() == HttpServletResponse.class){
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), i);
                    } else {
                        handler.getParamIndexMapping().put(parameter.getName(), i); // <name, 2>
                    }
                }

                // 建立url和Method之间的映射关系（map缓存起来）
                handlerMapping.add(handler);

                // 建立handler和security之间的映射关系
                if (!method.isAnnotationPresent(Security.class)){
                    // 如果在方法上没有@Security
                    if(!securities.isEmpty()){
                        // 但在Controller上有@Security，沿用Controller的安全验证
                        handlerSecuritiesMapping.put(handler, securities);
                    } else {
                        // 并且在Controller上也没有@Security，则无需验证，任何人都可访问
                        handlerSecuritiesMapping.put(handler, null);
                    }
                } else {
                    // 如果在方法上有@Security，先获取有权限的用户
                    Security security = method.getAnnotation(Security.class);
                    String[] values = security.value();
                    Set<String> methodSecurities = new HashSet<>();
                    for (String value : values) {
                        methodSecurities.add(value);
                    }

                    if (!securities.isEmpty()) {
                        // 取Controller和Method的Security的交集
                        Set<String> collect = methodSecurities.stream().filter(item -> securities.contains(item)).collect(Collectors.toSet());
                        handlerSecuritiesMapping.put(handler, collect);
                    } else {
                        // 如果controller上没有，则直接使用方法上的
                        handlerSecuritiesMapping.put(handler, methodSecurities);
                    }
                }
            }
        }
    }

    // 实现依赖注入
    private void doAutowired() {
        if(ioc.isEmpty()) return;

        // 有对象，再进行依赖注入处理
        // 遍历ioc中所有的对象，查看对象中的字段，是否有@MyAutowired注解，如果有需要维护依赖注入关系
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取bean对象中的字段信息
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();

            for (Field declaredField : declaredFields) {
                // @MyAutowired private IDemoService demoService;

                if(!declaredField.isAnnotationPresent(MyAutowired.class)) continue;

                // 有该注解
                MyAutowired annotation = declaredField.getAnnotation(MyAutowired.class);
                String beanName = annotation.value(); // 需要注入的bean的id
                if("".equals(beanName.trim())){
                    // 没有配置具体的bean id，那就需要根据当前字段类型注入（接口注入）IDemoService
                    beanName = declaredField.getType().getName();
                }

                // 开启赋值
                declaredField.setAccessible(true);
                try {
                    declaredField.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ioc容器
    // 基于classNames缓存的类的全限定类名，以及反射技术，完成对象创建和管理
    private void doInstance() {
        if (classNames.size() == 0) return;

        try {
            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i);//com.com.study.demo.controller.DemoController

                // 反射
                Class<?> aClass = Class.forName(className);
                if (aClass.isAnnotationPresent(MyController.class)) {
                    // controller的id此处不做过多处理，不取value类，用类的首字母小写作为id，保存到ioc中
                    String simpleName = aClass.getSimpleName(); // DemoController
                    String lowerFirstSimpleName = lowerFirst(simpleName); // demoController
                    Object instance = aClass.newInstance();
                    ioc.put(lowerFirstSimpleName, instance);
                } else if (aClass.isAnnotationPresent(MyService.class)) {
                    MyService annotation = aClass.getAnnotation(MyService.class);
                    Object instance = aClass.newInstance();

                    // 获取注解value的值
                    String beanName = annotation.value();

                    if (!"".equals(beanName.trim())) {
                        // 指定id，就以指定的为准
                        ioc.put(beanName, instance);
                    } else {
                        // 未指定id，就以类名首字母小写
                        beanName = lowerFirst(aClass.getSimpleName());
                        ioc.put(beanName, instance);
                    }

                    // service层往往是有接口的，面向接口，此时再以接口名放入ioc，便于后期根据接口类型注入
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        // 以接口的全限定类名作为id放入
                        ioc.put(anInterface.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 首字母小写
    private String lowerFirst(String str){
        char[] chars = str.toCharArray();
        if('A' <= chars[0] && chars[0] <= 'Z'){
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    // 扫描类
    // com.com.study.demo package ---> 磁盘上的文件夹（File）
    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.", "/");
        File pack = new File(scanPackagePath);
        File[] files = pack.listFiles();

        for (File file : files) {
            if(file.isDirectory()){ // 子package
                // 递归
                doScan(scanPackage + "." + file.getName());// com.com.study.demo.controller
            } else if(file.getName().endsWith(".class")) {
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    // 加载配置文件
    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
