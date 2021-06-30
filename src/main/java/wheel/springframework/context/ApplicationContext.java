package wheel.springframework.context;

/**
 * 应用程序上下文接口。
 * Spring的入口，这里主要实现BeanFactory的getBean功能
 */
public interface ApplicationContext {

    Object getBean(String beanName)  throws  Exception;

    Object getBean(Class clazz) throws Exception;

}
