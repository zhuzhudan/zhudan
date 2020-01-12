package com.springframework.context.support;

import com.springframework.annotation.*;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.support.BeanDefinitionReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * 通过xml读取配置信息
 */
public class XmlBeanDefinitionReader extends BeanDefinitionReader {
    public XmlBeanDefinitionReader(AbstractApplicationContext abstractApplicationContext) {
        super(abstractApplicationContext);
    }

    @Override
    public void loadBeanDefinitions(String location) {
        // 加载xml文件
        String path = location.substring("classpath:".length());

        try {
            InputStream inputStream = null;
            try {
                // 获得文件流
                inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
                // 实际执行加载xml

                // 读取xml信息
                Document document = new SAXReader().read(inputStream);
                Element rootElement = document.getRootElement();

                List<Element> childElementList = rootElement.elements();
                for (Element element : childElementList) {
                    String prefix = element.getNamespacePrefix();
                    if(prefix != null && !"".equals(prefix)){
                        // context标签
                        if(prefix.equals("context")){
                            String name = element.getName();
                            if("component-scan".equals(name)) {
                                // 获得扫描路径
                                String basePackage = element.attributeValue("base-package");
                                basePackage = basePackage.replace(",",";");
                                String[] basePackages = basePackage.split(";");

                                // 进行扫描，并注册BeanDefinition
                                List<BeanDefinition> beanDefinitionList = doScan(basePackages);
                            }
                        }
                    } else {
                        // bean标签
                        String id = element.attributeValue("id");
                        String className = element.attributeValue("class");

                        // 将bean标签封装成BeanDefinition
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setBeanClassName(className);
                        beanDefinition.setBeanName(id);

                        Map<String, Object> propertyValues = new HashMap<>();
                        List<Element> propertyList = element.elements("property");
                        for (Element property : propertyList) {
                            String name = property.attributeValue("name");
                            String value = property.attributeValue("value");
                            String ref = property.attributeValue("ref");
                            PropertyValue propertyValue = new PropertyValue(name, value, ref);
                            propertyValues.put(name, propertyValue);
                        }
                        if(propertyValues.size() != 0){
                            beanDefinition.setPropertyMap(propertyValues);
                        }

                        Map<String, Object> constructArgs = new HashMap<>();
                        List<Element> constructorArgList = element.elements("constructor-arg");
                        for (Element constructor : constructorArgList) {
                            String name = constructor.attributeValue("name");
                            String value = constructor.attributeValue("value");
                            String ref = constructor.attributeValue("ref");
                            PropertyValue propertyValue = new PropertyValue(name, value, ref);
                            constructArgs.put(name, propertyValue);
                        }
                        if(constructArgs.size() != 0){
                            beanDefinition.setConstrcutorArgs(constructArgs);
                        }

                        // 注册BeanDefinition
                        getAbstractApplicationContext().registerBeanDefinition(beanDefinition);
                    }
                }
            } finally {
                inputStream.close();
            }

        } catch (Exception e) {
            throw new RuntimeException("Exception parsing XML document");
        }

    }

    /**
     * 通过扫描base package，获得配置注解的bean，并封装成BeanDefinition进行注册
     */
    public List<BeanDefinition> doScan(String... basePackages){
        List<BeanDefinition> result = new LinkedList<>();
        for (String basePackage : basePackages) {
            List<BeanDefinition> beanDefinitionList = doScan(basePackage);
            for (BeanDefinition beanDefinition : beanDefinitionList) {
                // 注册BeanDefinition
                getAbstractApplicationContext().registerBeanDefinition(beanDefinition);

                result.add(beanDefinition);
            }

        }
        return result;
    }


    public List<BeanDefinition> doScan(String basePackage){
        Set<String> result = new LinkedHashSet<String>(8);
        getResource(basePackage, result);

        List<BeanDefinition> beanDefinitionList = new LinkedList<>();
        for (String beanName : result) {
            BeanDefinition beanDefinition = loadBeanDefinition(beanName);
            if(beanDefinition != null) {
                beanDefinitionList.add(beanDefinition);
            }
        }
        return beanDefinitionList;
    }

    /**
     * 通过配置文件中base-package，扫描包路径下的所有class文件，并把beanName存储
     */
    public void getResource(String basePackage, Set<String> result){
        String path = basePackage.replace(".", "/");
        try {
            Enumeration<URL> resourceUrls = this.getClass().getClassLoader().getResources(path);
            while (resourceUrls.hasMoreElements()){
                URL url = resourceUrls.nextElement();
                URI uri = new URI(url.toString().replace(" ","%20"));
                File basePackageFile = new File(uri.getSchemeSpecificPart());
                // 扫描此路径下的所有文件
                for (File file : basePackageFile.listFiles()) {
                    if(file.isDirectory()){
                        // 如果是文件夹继续扫描文件夹
                        getResource(basePackage+"."+file.getName(), result);
                    } else {
                        if(!file.getName().endsWith(".class")){
                            // 如果不是class文件文件跳过
                            continue;
                        }
                        // 是个class文件文件，获得文件名，即类名
                        String className = basePackage + "." + file.getName().replace(".class", "");
                        result.add(className);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public BeanDefinition loadBeanDefinition(String className){
        BeanDefinition beanDefinition = null;
        try {
            Class<?> beanClass = Class.forName(className);
            boolean isInterface = false;
            // 如果在接口上添加注解标识,则直接跳过
            if(beanClass.isInterface()){
//                return null;
                isInterface = true;
            }

            boolean isAnnotationBean = false;
            String beanName = null;

            // 是否是@Repository、@Component、@Service、@Controller
            Annotation[] beanAnnotations = beanClass.getAnnotations();
            for (Annotation beanAnnotation : beanAnnotations) {
                Class<? extends Annotation> annotationType = beanAnnotation.annotationType();
                if(annotationType == Repository.class){
                    isAnnotationBean = true;
                    Repository annotation = (Repository) beanClass.getAnnotation(annotationType);
                    beanName = annotation.value();
                } else if(annotationType == Component.class){
                    isAnnotationBean = true;
                    Component annotation = (Component) beanClass.getAnnotation(annotationType);
                    beanName = annotation.value();
                } else if (annotationType == Service.class){
                    isAnnotationBean = true;
                    Service annotation = (Service) beanClass.getAnnotation(annotationType);
                    beanName = annotation.value();
                } else if (annotationType == Controller.class){
                    isAnnotationBean = true;
                    Controller annotation = (Controller) beanClass.getAnnotation(annotationType);
                    beanName = annotation.value();
                } else if (annotationType == Transactional.class){
                    if(isInterface) {
                        this.getAbstractApplicationContext().addToTransactionalInterface(beanClass);
                        return null;
                    }
                }
            }

            // 为BeanDefinition赋值
            if(isAnnotationBean) {
                beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClassName(className);
                beanDefinition.setBeanClass(beanClass);
                beanDefinition.setAnnotationClass(beanAnnotations);
                if(beanName != null && !"".equals(beanName)){
                    beanDefinition.setBeanName(beanName);
                } else {
                    beanDefinition.setBeanName(toLowerCaseFirstOne(beanClass.getSimpleName()));
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return beanDefinition;
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

}
