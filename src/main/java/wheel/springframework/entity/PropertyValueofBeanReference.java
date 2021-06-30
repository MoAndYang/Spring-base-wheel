package wheel.springframework.entity;

/**
 * Bean单属性（引用类型）的抽象实体，包含getter和setter.
 * 注入对象类型为引用，对引用进行定义（全限定类名-实例对象），xml文件中使用ref
 */
public class PropertyValueofBeanReference {
    private String name;  //引用类型的全限定类名
    private Object bean;  //引用类型的对象也是一个bean实例化对象

    public PropertyValueofBeanReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
