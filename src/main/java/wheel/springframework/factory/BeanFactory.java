package wheel.springframework.factory;

import wheel.springframework.entity.BeanDefinition;

/**
 * Bean工厂抽象接口。
 * 定义通用的工厂操作方法
 */
public interface BeanFactory {

    /**
     * 向工厂中注册Bean的定义信息的抽象实体
     * @param name bean的id
     * @param beanDefinition bean的定义实体对象
     * @throws Exception
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;

    /**
     * 根据类的全限定名称从容器中获取bean
     * @param name bean的id
     * @return bean实例对象
     * @throws Exception
     */
    Object getBean(String name) throws  Exception;

    /**
     * 根据类的类型从容器中获取bean
     * @param clazz bean所属的类
     * @return bean
     * @throws Exception
     */
    Object getBean(Class clazz) throws  Exception;
}
