package wheel.springframework.entity;


/**
 * Bean的定义信息的抽象实体，包含getter和setter。
 * 从配置文件中读取的Bean的定义
 */
public class BeanDefinition {
    /** 实例化后的对象 */
    private Object bean;   //bean实例化对象
    private Class beanClass;  //bean的类定义
    private String beanClassName; //bean的全限定类名
    private Boolean singleton;  //bean是否被设定为单例模式（否则代表原型模式）
    private PropertyValues propertyValues;  //bean全部属性的抽象实体

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }


    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        try {
            this.beanClass = Class.forName(beanClassName);  //读取配置文件后直接反射获得Class对象
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isSingleton(){return singleton;}

    public PropertyValues getPropertyValues() {
        if(propertyValues == null){
            propertyValues = new PropertyValues();  //如果没有属性，也创建一个空属性实体
        }
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
}
