package com.springframework.context.support;

import com.springframework.beans.support.DefaultListableBeanFactory;

/**
 * 通过xml读取配置文件
 */
public class ClassPathXmlApplicationContext extends DefaultListableBeanFactory {
    // 配置文件位置
    private String[] configLocations;

    public void setConfigLocations(String[] configLocations) {
        if(configLocations != null) {
            this.configLocations = new String[configLocations.length];
            for (int i = 0; i < configLocations.length; i++) {
                this.configLocations[i] = configLocations[i].trim();
            }
        } else {
            this.configLocations = null;
        }
    }

    public ClassPathXmlApplicationContext(String configLocation) {
        this(new String[]{configLocation}, true);
    }

    public ClassPathXmlApplicationContext(String... configLocations) {
        this(configLocations, true);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) {
        // 获取配置文件地址
        setConfigLocations(configLocations);

        // 完成Spring容器的初始化
        if(refresh){
            refresh();
        }
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // 给beanFactory创建一个XmlBeanDefinitionReader读取器，解析xml
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // 加载BeanDefinitions
        loadBeanDefinitions(beanDefinitionReader);


    }

    /**
     * 真正解析xml，加载BeanDefinitions
     */
    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader){
        reader.loadBeanDefinitions(this.configLocations);
    }

}
