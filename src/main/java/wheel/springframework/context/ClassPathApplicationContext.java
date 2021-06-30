package wheel.springframework.context;


import wheel.springframework.entity.BeanDefinition;
import wheel.springframework.factory.AbstractBeanFactory;
import wheel.springframework.factory.AutowireCapableBeanFactory;
import wheel.springframework.io.ResourceLoader;
import wheel.springframework.reader.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * 应用程序上下文具体类。
 * 创建一个 BeanDefinitionReader 读取配置文件，并且将读取到的配置存到 BeanFactory 中，并且由 BeanFactory 创建对应的实例对象
 */
public class ClassPathApplicationContext extends AbstractApplicationContext {

    private final Object startupShutDownMonitor = new Object();
    private  String location; //配置文件位置

    public ClassPathApplicationContext(String location) throws Exception {
        this.location = location;
        refresh();
    }

    /**
     * 刷新bean，从文件中读取并解析注入（public代表可以外部多次刷新）
     * @throws Exception
     */
    public void refresh() throws Exception {
        synchronized (startupShutDownMonitor){  //加锁，保证Bean信息的并发安全
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            prepareBeanFactory(beanFactory);
            this.beanFactory = beanFactory;
        }
    }

    /**
     * 创建Bean工厂，这个工厂加载资源并将其都注册到bean工厂中去
     * @return  已经完成资源加载和注册的bean工厂对象实例
     * @throws Exception
     */
    private AbstractBeanFactory obtainBeanFactory() throws Exception {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        beanDefinitionReader.loadBeanDefinitions(location);
        AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionReader.getRegistry().entrySet()){
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        return beanFactory;
    }

    /**
     * 填充Bean对象，遍历已注册的Bean然后根据Bean的定义信息创建Bean的实例对象
     * @param beanFactory 已经完成资源加载和注册的bean工厂对象实例
     * @throws Exception
     */
    private void prepareBeanFactory(AbstractBeanFactory beanFactory) throws Exception {
        beanFactory.populateBeans();

    }


}
