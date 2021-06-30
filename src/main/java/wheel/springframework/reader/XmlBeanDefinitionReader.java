package wheel.springframework.reader;

import org.reflections.Reflections;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import wheel.springframework.annotation.*;
import wheel.springframework.entity.BeanDefinition;
import wheel.springframework.entity.PropertyValue;
import wheel.springframework.entity.PropertyValueofBeanReference;
import wheel.springframework.io.ResourceLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * XML配置文件形式的Bean定义信息读取器
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader{

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    /**
     * 实际执行资源读取后的字符输入流加载工作
     * @param inputStream 资源读取后的字符输入流
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        //inputStream解析为xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);  //将给定的InputStream的内容解析为XML文档，并返回一个新的DOM Document对象。
        //xml DOM对象进行解析并注册bean
        registerBeanDefinitions(document);
        inputStream.close(); //关闭此输入流并释放与该流关联的所有系统资源。
    }

    /**
     * xml DOM对象进行解析并注册bean
     * @param document 标识xml文件的DOM对象
     */
    public void registerBeanDefinitions(Document document){
        Element root = document.getDocumentElement();
        //从文件根递归解析
        parseBeanDeifinitions(root);
    }

    /**
     * 【核心类】
     * 从根标签递归解析,从此处开始分类：是注解解析还是xml解析
     * @param root 根标签
     */
    protected void parseBeanDeifinitions(Element root){
        NodeList nodeList = root.getChildNodes();
        //确定是否是注解的配置文件
        String basePackage = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            if(nodeList.item(i) instanceof Element){
                Element ele = (Element) nodeList.item(i);
                if(ele.getTagName().equals("component-scan")){
                    basePackage = ele.getAttribute("base-package");
                    break;
                }
            }
        }
        //注解配置文件，从注解解析注入
        if(basePackage != null){
            parseAnnotation(basePackage);
            return;
        }

        //非注解配置文件，从xml标签解析注入
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node instanceof  Element){
                Element ele = (Element) node;
                if(ele.getTagName().equals("bean")){
                    processBeanDefinition(ele);
                }
            }
        }
    }

    /**
     *  解析单个bean标签的配置信息并注册该bean
     * @param ele 标签对象
     */
    protected void processBeanDefinition(Element ele){
        String name = ele.getAttribute("id");
        String className = ele.getAttribute("class");
        boolean singleton = true;    //默认单例模式
        if(ele.hasAttribute("scope") && "prototype".equals(ele.getAttribute("scope"))){
            singleton = false;   //原型模式
        }
        //根据配置文件完善Bean定义信息
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(ele, beanDefinition);
        beanDefinition.setBeanClassName(className);
        beanDefinition.setSingleton(singleton);
        //注册到读取器的哈希表
        getRegistry().put(name, beanDefinition);
    }

    /**
     * 解析单个bean标签的下级标签完善bean定义信息的属性信息
     * @param ele  标签对象
     * @param beanDefinition Bean定义信息对象
     */
    protected void processProperty(Element ele, BeanDefinition beanDefinition){
        NodeList propertyNodeList = ele.getElementsByTagName("property");
        for (int i = 0; i < propertyNodeList.getLength(); i++) {
            Node node = propertyNodeList.item(i);
            if(node instanceof  Element){
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if(value != null && value.length() > 0){
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name,value));
                }else{
                    String ref = propertyEle.getAttribute("ref");
                    if(ref == null || ref.length() == 0){
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '" + name + "' must specify a ref or value");
                    }
                    PropertyValueofBeanReference propertyValueofBeanReference = new PropertyValueofBeanReference(ref);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, propertyValueofBeanReference));
                }
            }
        }
    }


    /**
     * 获取到目标包{@code basePackage}下所有的类，并遍历解析
     * @param basePackage
     */
    protected void parseAnnotation(String basePackage){
        Set<Class<?>> classes = getClasses(basePackage);  //获取到{@code basePackage}包下的所有类
        for (Class clazz : classes) {
            processAnnotationBeanDefinition(clazz);  //解析每一个类的注解
        }
    }

    /**
     * 获取到{@code basePackage}包下的所有类
     * @param packageName
     * @return
     */
    protected Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;  //是否递归获取
        String packageDirName = packageName.replace('.','/');
        Enumeration<URL>  dirs;  //定义一个枚举的集合 并进行循环来处理这个目录
        try{
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代
            while(dirs.hasMoreElements()){
                //获取下一个元素
                URL url = dirs.nextElement();
                //得到协议名称
                String protocol = url.getProtocol();
                if("file".equals(protocol)){
                    // 如果是文件的形式
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件，并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }else if("jar".equals(protocol)){
                    //如果是以jar包的形式
                    //定义一个JarFile
                    JarFile jar;
                    try{
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的方式扫描整个包下的文件，并添加到集合中
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 利用了反射机制来获取类上的注解，以判断对应的类是否是需要注册的 bean，并寻找相关的注解，如 @Scope，形成对应的 BeanDefinition
     * @param clazz
     */
    protected void processAnnotationBeanDefinition(Class<?> clazz){
        //如果此元素上存在指定类型的注释，则返回 true，否则返回 false。
        if(clazz.isAnnotationPresent(Component.class)){
            String name = clazz.getAnnotation(Component.class).value();  //获取name参数作为id name
            if("".equals(name)){
                name = clazz.getName();  //如果没获取到参数，就获取当前类名作为id name
            }
            String className = clazz.getName();
            boolean singleton = true;
            if(clazz.isAnnotationPresent(Scope.class) && "prototype".equals(clazz.getAnnotation(Scope.class).value())){
                singleton = false;
            }
            BeanDefinition beanDefinition = new BeanDefinition();
            processAnnotationProperty(clazz, beanDefinition);
            beanDefinition.setBeanClassName(className);
            beanDefinition.setSingleton(singleton);
            getRegistry().put(name, beanDefinition);
        }
    }

    /**
     * 对类的每一个属性进行判断，来确定每个属性是否需要注入等
     * @param clazz
     * @param beanDefinition
     */
    protected void processAnnotationProperty(Class<?> clazz, BeanDefinition beanDefinition) {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            String name = field.getName();
            if(field.isAnnotationPresent(Value.class)){
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                if(value.length() > 0){
                    //优先进行设值注入
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name,value));
                }
            }else if(field.isAnnotationPresent(Autowired.class)){
                if(field.isAnnotationPresent(Qualifier.class)){
                    Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                    String refValue = qualifierAnnotation.value();
                    if("".equals(refValue)){
                        throw new IllegalArgumentException("The value of  Qualifier should not be null!");
                    }
                    PropertyValueofBeanReference beanReference = new PropertyValueofBeanReference(refValue);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }else{
                    String refValue = field.getType().getName(); //否则使用引用类型名
                    PropertyValueofBeanReference beanReference = new PropertyValueofBeanReference(refValue);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
    }
}
