package wheel.ioc;

import org.junit.Test;
import wheel.ioc.service.annotationservice.HelloWorldServiceBaseAnnotation;
import wheel.ioc.service.annotationservice.WrapHelloWorldServiceBaseAnnotation;
import wheel.springframework.context.ApplicationContext;
import wheel.springframework.context.ClassPathApplicationContext;

public class AnnotationIoCTest {


    /**
     * 引用类型设值注入测试
     * @throws Exception
     */
    @Test
    public void referenceInjectTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC-annotation.xml");
        WrapHelloWorldServiceBaseAnnotation wrapHelloWorldService = (WrapHelloWorldServiceBaseAnnotation) applicationContext.getBean("wrapAnnotationService");
        wrapHelloWorldService.sayHello();
    }


    /**
     * 单例模式测试
     * @throws Exception
     */
    @Test
    public void singleModeTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC-annotation.xml");
        HelloWorldServiceBaseAnnotation helloWorldServiceBaseAnnotation1 = (HelloWorldServiceBaseAnnotation) applicationContext.getBean("singleHelloWorldAnnotationService");
        HelloWorldServiceBaseAnnotation helloWorldServiceBaseAnnotation2 = (HelloWorldServiceBaseAnnotation) applicationContext.getBean("singleHelloWorldAnnotationService");
        helloWorldServiceBaseAnnotation1.sayHello();
        helloWorldServiceBaseAnnotation2.sayHello();
        System.out.println("singleton验证相等: " + (helloWorldServiceBaseAnnotation1 == helloWorldServiceBaseAnnotation2));
    }

    /**
     * 多例模式测试
     * @throws Exception
     */
    @Test
    public void protoModeTest() throws Exception {
        ApplicationContext applicationContext = new ClassPathApplicationContext("application-IoC-annotation.xml");
        HelloWorldServiceBaseAnnotation helloWorldServiceBaseAnnotation1 = (HelloWorldServiceBaseAnnotation) applicationContext.getBean("protoHelloWorldAnnotationService");
        HelloWorldServiceBaseAnnotation helloWorldServiceBaseAnnotation2 = (HelloWorldServiceBaseAnnotation) applicationContext.getBean("protoHelloWorldAnnotationService");
        helloWorldServiceBaseAnnotation1.sayHello();
        helloWorldServiceBaseAnnotation2.sayHello();
        System.out.println("prototype验证相等: " + (helloWorldServiceBaseAnnotation1 == helloWorldServiceBaseAnnotation2));
    }



}
