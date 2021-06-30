package wheel.ioc.service.xmlservice;


public class WrapHelloWorldServiceBaseXml {

    /**
     * 属性是引用数据类型
     */
    private HelloWorldServiceBaseXml helloWorldServiceBaseXml;

    public void sayHello(){
        helloWorldServiceBaseXml.sayHello();
    }
}
