package wheel.springframework.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean全部属性的抽象实体，包含getter。
 * 键值对组，表示注入对象的属性
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void addPropertyValue(PropertyValue propertyValue){
        this.propertyValueList.add(propertyValue);
    }
}
