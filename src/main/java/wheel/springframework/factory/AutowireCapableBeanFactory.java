package wheel.springframework.factory;

import wheel.springframework.entity.BeanDefinition;
import wheel.springframework.entity.PropertyValue;
import wheel.springframework.entity.PropertyValueofBeanReference;

import java.lang.reflect.Field;


/**
 * 自动注入属性的Bean工厂类。
 * 可以创建完成实例对象后，注入其中的属性，如果属性是一个对象引用，那么就再去创建那个被引用的实例对象，并递归地完成属性注入。
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory{

    @Override
    Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        if (beanDefinition.isSingleton() && beanDefinition.getBean() != null){
            return beanDefinition.getBean();  //首先排除bean是单例模式且已经创建过的情况，此时直接返回bean的单例对象
        }
        Object bean = beanDefinition.getBeanClass().newInstance(); //创建一个bean实例对象
        if(beanDefinition.isSingleton()){
            beanDefinition.setBean(bean); //此处判断结果是bean是单例模式但是还没有创建过对象，将新创建的对象记录在bean的定义信息中。
        }
        applyPropertyValue(bean, beanDefinition); //无论是否是单例模式，bean对象创建后都要注入bean对象得属性，并记录在bean的定义信息中
        return bean;
    }

    /**
     * 为新创创建的bean对象注入属性，并记录在bean的定义信息中。注意属性为引用类型时，就再去创建那个被引用的实例对象，并递归地完成属性注入。
     * @param bean  bean实例对象
     * @param beanDefinition  对应的bean的定义信息
     */
    void applyPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()){  //得到bean对象的所有属性
            Field field = bean.getClass().getDeclaredField(propertyValue.getName());
            Object value = propertyValue.getValue();
            if(value instanceof PropertyValueofBeanReference){  //属性是引用类型
                PropertyValueofBeanReference propertyValueofBeanReference = (PropertyValueofBeanReference) propertyValue.getValue();
                //1. 优先尝试按照注册的id name匹配
                //以下代码与AbstractBeanFactory类中getBean(String name)方法中相关代码一致
                BeanDefinition refDefinition = beanDefinitionConcurrentHashMap.get(propertyValueofBeanReference.getName());
                if(refDefinition != null){
                    if(!refDefinition.isSingleton() || refDefinition.getBean() == null){
                        value = doCreateBean(refDefinition);
                    }else{
                        value = refDefinition.getBean();
                    }
                }
                else{
                    //2. 尝试按照类型匹配，返回第一个匹配项(不确保是单例还是多例)
                    //以下代码与AbstractBeanFactory类中getBean(Class clazz)方法中相关代码一致
                    Class clazz = Class.forName(propertyValueofBeanReference.getName());
                    for(BeanDefinition definition : beanDefinitionConcurrentHashMap.values()){
                        if(clazz.isAssignableFrom(definition.getBeanClass())){
                            if(!definition.isSingleton() || definition.getBean() == null){
                                value = doCreateBean(definition);
                            }else{
                                value = definition.getBean();
                            }
                            break;
                        }
                    }
                }
            }

            if(value == null){
                throw new RuntimeException("无法注入引用类型的属性");
            }
            field.setAccessible(true);
            field.set(bean, value);
        }
    }
}
