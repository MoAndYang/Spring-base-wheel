package wheel.springframework.reader;

/**
 * Bean定义信息读取器抽象接口
 */
public interface BeanDefinitionReader {
    /**
     * 在location处读取Beans的配置文件
     * @param location 配置文件所在路径
     * @throws Exception
     */
    void loadBeanDefinitions(String location) throws Exception;
}
