package wheel.springframework.entity;

/**
 * Bean单属性（全部类型）的抽象实体，包含getter.
 * 单个键值对，表示注入对象的属性，xml文件中使用value和ref
 */
public class PropertyValue {

    private final  String name;   //final修饰保证共享变量的可见性
    private final  Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
