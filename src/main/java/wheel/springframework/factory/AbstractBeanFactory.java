package wheel.springframework.factory;


import wheel.springframework.entity.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工厂抽象类。
 * 实现通用的工厂操作方法
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    ConcurrentHashMap<String, BeanDefinition> beanDefinitionConcurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 根据Bean的定义信息创建Bean的实例对象,交给具体的工厂去实现
     * @param beanDefinition Bean定义信息的实体对象
     * @return  Bean实例对象
     * @throws Exception
     */
    abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;

    /**
     * 填充Bean对象，遍历已注册的Bean然后根据Bean的定义信息创建Bean的实例对象
     * @throws Exception
     */
    public void populateBeans() throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionConcurrentHashMap.entrySet()) {
            doCreateBean(entry.getValue());
        }
    }


    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanDefinitionConcurrentHashMap.put(name, beanDefinition);
    }

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionConcurrentHashMap.get(name);
        if(beanDefinition == null){
            throw new RuntimeException("Unable to find the bean of this id name, please check!");
        }
        if(!beanDefinition.isSingleton() || beanDefinition.getBean() == null){
            return doCreateBean(beanDefinition);  //Bean不要求单例或者Bean对象还未创建过时，创建新的bean对象
        }else{
            return beanDefinition.getBean();      //Bean要求单例且Bean对象已经创建过，直接返回该Bean对象
        }
    }

    /**
     * 根据类的类型返回一个bean对象，但是不保证是单例还是多例(这取决于xml文件的配置以及遍历map容器时第一个被遍历到的合适对象)
     * @param clazz bean所属的类
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(Class clazz) throws Exception {
        BeanDefinition beanDefinition = null;
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionConcurrentHashMap.entrySet()) {
            Class tempClass = entry.getValue().getBeanClass();
            if(clazz.isAssignableFrom(tempClass)){
                beanDefinition = entry.getValue();  //根据类型获取bean对象时，首先遍历已经注册的bean，如果可以找到该类型或该类型派生自已注册bean，直接返回已注册bean
                break;
            }
        }
        //以下代码与getBean(String name)方法中代码一致
        if(beanDefinition == null){
            throw  new RuntimeException("Unable to find the bean of this class type, please check!");
        }
        if(!beanDefinition.isSingleton() || beanDefinition.getBean() == null){
            return doCreateBean(beanDefinition);
        }
        else{
            return beanDefinition.getBean();
        }
    }


}
