package wheel.springframework.context;

import wheel.springframework.factory.BeanFactory;


/**
 *  应用程序上下文抽象类。
 *  实现一些通用的方法
 */
public class AbstractApplicationContext implements ApplicationContext {

    BeanFactory beanFactory;

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }
}
