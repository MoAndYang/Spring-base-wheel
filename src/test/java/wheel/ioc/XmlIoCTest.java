package wheel.ioc;

import org.junit.Test;
import wheel.ioc.service.xmlservice.HelloWorldServiceBaseXml;
import wheel.ioc.service.xmlservice.WrapHelloWorldServiceBaseXml;
import wheel.ioc.service.xmlservice.impl.HelloWorldServiceBaseXmlImpl;
import wheel.springframework.context.ApplicationContext;
import wheel.springframework.context.ClassPathApplicationContext;

public class XmlIoCTest {

    /**
     * 引用类型设值注入测试
     * @throws Exception
     */
    @Test
    public void referenceInjectTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC.xml");
        WrapHelloWorldServiceBaseXml wrapHelloWorldService = (WrapHelloWorldServiceBaseXml) applicationContext.getBean("wrapHelloWorldService");
        wrapHelloWorldService.sayHello();
    }

    /**
     * 单例模式测试
     * @throws Exception
     */
    @Test
    public void singleModeTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC.xml");
        HelloWorldServiceBaseXml helloWorldServiceBaseXml1 = (HelloWorldServiceBaseXml) applicationContext.getBean("singleHelloWorldService");
        HelloWorldServiceBaseXml helloWorldServiceBaseXml2 = (HelloWorldServiceBaseXml) applicationContext.getBean("singleHelloWorldService");
        System.out.println("singleton验证相等: " + (helloWorldServiceBaseXml1 == helloWorldServiceBaseXml2));
    }

    /**
     * 多例模式测试
     * @throws Exception
     */
    @Test
    public void protoModeTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC.xml");
        HelloWorldServiceBaseXml helloWorldServiceBaseXml1 = (HelloWorldServiceBaseXml) applicationContext.getBean("protoHelloWorldService");
        HelloWorldServiceBaseXml helloWorldServiceBaseXml2 = (HelloWorldServiceBaseXml) applicationContext.getBean("protoHelloWorldService");
        System.out.println("prototype验证相等: " + (helloWorldServiceBaseXml1 == helloWorldServiceBaseXml2));
    }

    /**
     * 根据类型获取bean对象测试，但是不保证返回的bean对象时单例模式下的还是多例模式下的，慎用
     * @throws Exception
     */
    @Test
    public void typeInjectTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC.xml");
        HelloWorldServiceBaseXml helloWorldServiceBaseXml = (HelloWorldServiceBaseXml) applicationContext.getBean(HelloWorldServiceBaseXmlImpl.class);
        helloWorldServiceBaseXml.sayHello();
    }
}
