package wheel.springframework.reader;

import wheel.springframework.entity.BeanDefinition;
import wheel.springframework.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean定义信息读取器抽象类。
 * 实现通用方法
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private Map<String, BeanDefinition> registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.registry = new HashMap<>();
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
