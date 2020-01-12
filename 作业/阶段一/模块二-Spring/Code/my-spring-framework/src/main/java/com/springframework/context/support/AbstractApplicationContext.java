package com.springframework.context.support;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Transactional;
import com.springframework.aop.AopProxy;
import com.springframework.aop.CglibAopProxy;
import com.springframework.aop.JdkDynamicAopProxy;
import com.springframework.aop.config.AopConfig;
import com.springframework.aop.support.AdvisedSupport;
import com.springframework.beans.BeanFactory;
import com.springframework.beans.BeanWrapper;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.jdbc.DataSourceTransactionManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象ApplicationContext实现ApplicationContext接口，并实现一些方法
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    // context启动时间
    private long startupDate;

    // 当前context是否激活标识
    private AtomicBoolean active = new AtomicBoolean();

    // 当前context是否已经关闭
    private AtomicBoolean closed = new AtomicBoolean();

    // 对象锁，并发时锁定，防止多次初始化
    private Object startupShutdownMonitor = new Object();

    // 同步初始化BeanFactory
    private final Object beanFactoryMonitor = new Object();

    // BeanFactory
    private DefaultListableBeanFactory beanFactory;

    protected Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    private Set<Class> transactionalInterface = new HashSet<>();

    public Set<Class> getTransactionalInterface() {
        return transactionalInterface;
    }

    public void setTransactionalInterface(Set<Class> transactionalInterface) {
        this.transactionalInterface = transactionalInterface;
    }

    public void addToTransactionalInterface(Class interfaceName){
        this.transactionalInterface.add(interfaceName);
    }

    public void refresh(){
        synchronized (this.startupShutdownMonitor){
            // 刷新前预处理
            prepareRefresh();

            // 扫描配置文件文件，并提取创建bean，封装为BeanDefinition
            AbstractApplicationContext beanFactory = obtainFreshBeanFactory();

            // DI开始，初始化所有剩下的非懒加载的单例bean，填充属性
            finishBeanFactoryInitialization(beanFactory);
        }
    }


    protected void prepareRefresh(){
        // 设置启动时间和活动标识
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);

        // 一些环境配置可在以后进行配置
    }

    protected AbstractApplicationContext obtainFreshBeanFactory(){
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected final void refreshBeanFactory(){
        if(hasBeanFactory()){
            destroyBeans();
            closeBeanFactory();
        }

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        loadBeanDefinitions(beanFactory);
        synchronized (this.beanFactoryMonitor){
            this.beanFactory = beanFactory;
        }
    }

    /**
     * 判断BeanFactory是否已经创建
     */
    protected final boolean hasBeanFactory(){
        synchronized (this.beanFactoryMonitor){
            return (this.beanFactory != null);
        }
    }

    /**
     * 获得BeanFactory
     * @return
     */
    protected final AbstractApplicationContext getBeanFactory(){
        synchronized (this.beanFactoryMonitor){
            if(this.beanFactory == null){
                throw new IllegalStateException("BeanFactory初始化失败或已经关闭");
            }
            return this.beanFactory;
        }
    }

    public void setBeanFactory(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 销毁BeanFactory产生的Beans
     */
    protected void destroyBeans(){
        // 清除bean操作

    }

    /**
     * 关闭BeanFactory
     */
    protected final void closeBeanFactory(){
        synchronized (this.beanFactoryMonitor){
            if(this.beanFactory != null){
                this.beanFactory = null;
            }
        }
    }

    protected void prepareBeanFactory(AbstractApplicationContext beanFactory){
//        beanFactory.
    }

    /**
     * 模版模式的钩子方法--需要在子类中实现
     * 通过读取配置文件加载BeanDefinition
     * @param beanFactory
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);


    public abstract int getBeanDefinitionCount();

    public abstract void registerBeanDefinition(BeanDefinition beanDefinition);

    public abstract void preInstantiateSingletons();

    public abstract void registerSingleton(String beanName, Object singletonObject);

    public abstract void registerEarlySingleton(String beanName, Object earlySingletonObject);

    protected void finishBeanFactoryInitialization(AbstractApplicationContext beanFactory) {
        // 实例化所有立即加载的单例bean
        beanFactory.preInstantiateSingletons();
    }


    /**
     * 通过beanName创建单例bean
     * 在此不考虑循环依赖，不考虑多例情况
     */
    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().doGetBean(beanName);
    }

    @Override
    public Object getBean(Class clazz) {
        String beanDefinitionByClassName = this.beanFactory.getBeanDefinitionByClassName(clazz.getName());
        return getBeanFactory().doGetBean(beanDefinitionByClassName);
    }

    public <T> T doGetBean(String beanName){
        synchronized (this.singletonObjects) {
            BeanWrapper singletonObject = (BeanWrapper) this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                // 首先获取bean的配置
                BeanDefinition beanDefinition = this.beanFactory.getBeanDefinitionByBeanName(beanName);

                // 实际创建bean
                singletonObject = createBean(beanName, beanDefinition);

                // 将创建的bean加入到singletonObjects中
                registerSingleton(beanName, singletonObject);
            }
            return (T) singletonObject.getWrappedInstance();
        }
    }

    /**
     * 实际创建bean
     * @param beanName
     * @return
     */
    public BeanWrapper createBean(String beanName, BeanDefinition beanDefinition){
        return doCreateBean(beanName, beanDefinition);
    }

    public BeanWrapper doCreateBean(String beanName, BeanDefinition beanDefinition){
        BeanWrapper instanceWrapper = createBeanInstance(beanName, beanDefinition);

        // bean属性填充，DI开始
        populateBean(beanName, beanDefinition, instanceWrapper);

        instanceWrapper = scanTransactional(beanName, beanDefinition, instanceWrapper);

        return instanceWrapper;
    }

    /**
     * 通过反射创建bean实例，完成构造方法，完成创建对象
     */
    private BeanWrapper createBeanInstance(String beanName, BeanDefinition beanDefinition){
        Object beanInstance = null;
        try {
            Class<?> beanClass = beanDefinition.resolveBeanClass();

            // 获得构造函数参数
            Map<String, Object> constrcutorArgs = beanDefinition.getConstrcutorArgs();
            Class<?>[] constructorClass = new Class[constrcutorArgs.size()];
            Object[] constructorArgValues = new Object[constrcutorArgs.size()];
            int i = 0;
            for (Map.Entry<String, Object> constrcutorArg : constrcutorArgs.entrySet()) {
                PropertyValue value = (PropertyValue) constrcutorArg.getValue();
                if(value.getRef() != null && !"".equals(value.getRef())) {
                    BeanDefinition beanDefinitionByBeanName = this.beanFactory.getBeanDefinitionByBeanName(value.getRef());
                    constructorClass[i] = Class.forName(beanDefinitionByBeanName.getBeanClassName());

                    Object bean = getBean(value.getRef());
                    constructorArgValues[i++] = bean;
                }

            }
            Constructor<?> constructor = beanClass.getDeclaredConstructor(constructorClass);
            constructor.setAccessible(true);
            // 调用构造函数创建bean
            beanInstance = constructor.newInstance(constructorArgValues);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        BeanWrapper beanWrapper = new BeanWrapper(beanInstance);
        registerSingleton(beanName, beanWrapper);
        return beanWrapper;
    }

    // 填充属性
    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper){
        Class<?> clazz = beanWrapper.getWrappedClass();
        // 获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = toLowerCaseFirstOne(field.getName());
            }

            field.setAccessible(true);

            BeanWrapper autowiredBeanWrapper = (BeanWrapper) this.singletonObjects.get(autowiredBeanName);
            Object autowiredBean = null;
            if(autowiredBeanWrapper == null){
                autowiredBean = getBean(autowiredBeanName);
            } else {
                autowiredBean = autowiredBeanWrapper.getWrappedInstance();
            }

            try {
                field.set(beanWrapper.getWrappedInstance(), autowiredBean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        // 执行set方法
        // 获取包括父类的set方法
        List<Method> methodList = new ArrayList<>();
        while (clazz != null){
            methodList.addAll(new ArrayList<>(Arrays.asList(clazz.getMethods())));
            clazz = clazz.getSuperclass();
        }
        Method[] methods = new Method[methodList.size()];
        methodList.toArray(methods);
        for(Method method : methods){
            Map<String, Object> propertyMap = beanDefinition.getPropertyMap();
            for (Map.Entry<String, Object> stringObjectEntry : propertyMap.entrySet()) {

                String name = stringObjectEntry.getKey();
                String methodName = "set"+toUpperCaseFirstOne(name);

                if(methodName.equals(method.getName())){
                    method.setAccessible(true);
                    PropertyValue property = (PropertyValue) stringObjectEntry.getValue();
                    String value = property.getValue();
                    if(value!= null && !"".equals(value)){
                        try {
                            method.invoke(beanWrapper.getWrappedInstance(), value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String ref = property.getRef();
                        BeanWrapper propertyBeanWrapper = (BeanWrapper) this.singletonObjects.get(ref);
                        Object bean = null;
                        if(propertyBeanWrapper == null){
                            bean = getBean(ref);
                        } else {
                            bean = propertyBeanWrapper.getWrappedInstance();
                        }
                        try {
                            method.invoke(beanWrapper.getWrappedInstance(), bean);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

        }
    }

    // 将字符串的首字母小写
    private static String toLowerCaseFirstOne(String s){
        char[] chars = s.toCharArray();
        if(Character.isLowerCase(chars[0])){
            return s;
        }
        chars[0] += 32;
        return String.valueOf(chars);
    }
    // 将字符串的首字母大写
    private static String toUpperCaseFirstOne(String s){
        char[] chars = s.toCharArray();
        if(Character.isUpperCase(chars[0])){
            return s;
        }
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    private BeanWrapper scanTransactional(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        // 获取bean
        Object beanInstance = beanWrapper.getWrappedInstance();
        Class<?> beanClass = beanDefinition.resolveBeanClass();

        Annotation[] annotationClassList = (Annotation[]) beanDefinition.getAnnotationClass();
        if (annotationClassList != null) {
            for (Annotation annotationClass : annotationClassList) {
                Class<? extends Annotation> annotationType = annotationClass.annotationType();
                if (annotationType == Transactional.class) {
                    Object transactionalInstance = getBean(DataSourceTransactionManager.class);
                    // AOP
//                    AdvisedSupport advisedSupport = instantionAopConfig(beanDefinition);
                    AopConfig config = new AopConfig();
                    config.setPointCut("public .* " + ((Class) beanDefinition.getBeanClass()).getName() + "..*(.*)");
                    config.setAspectBefore("beginTransaction");
                    config.setAspectAfter("commit");
                    config.setAspectClass(DataSourceTransactionManager.class.getName());
                    config.setAspectAfterThrow("rollback");
                    config.setAspectAfterThrowingName("java.lang.Exception");
                    config.setAspectObject(transactionalInstance);

                    AdvisedSupport advisedSupport = new AdvisedSupport(config);
                    advisedSupport.setTargetClass(beanClass);
                    advisedSupport.setTarget(beanInstance);

                    // 创建代理策略，看是用CGLib还是JDK
                    beanInstance = createProxy(advisedSupport).getProxy();

                    beanWrapper = new BeanWrapper(beanInstance);
                    registerSingleton(beanName, beanWrapper);
                    return beanWrapper;
                }
            }
        }

        return beanWrapper;

    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
//                    public .* com.study.spring.business.service.. *Service..*(.*)
        config.setPointCut(((Class)beanDefinition.getBeanClass()).getName());
        config.setAspectBefore("beginTransaction");
        config.setAspectAfter("commit");
        config.setAspectClass(DataSourceTransactionManager.class.getName());
        config.setAspectAfterThrow("rollback");
        config.setAspectAfterThrowingName("java.lang.Exception");
        return new AdvisedSupport(config);
    }

    // 创建代理
    private AopProxy createProxy(AdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length> 0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }
}
