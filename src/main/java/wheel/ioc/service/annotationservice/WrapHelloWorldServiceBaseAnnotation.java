package wheel.ioc.service.annotationservice;

import wheel.springframework.annotation.Autowired;
import wheel.springframework.annotation.Component;

@Component("wrapAnnotationService")
public class WrapHelloWorldServiceBaseAnnotation {

    /**
     * 属性是引用数据类型
     * //不写@Qualifier(name)则按类型进行匹配，找到HelloWorldServiceBaseAnnotation或其子类或其实现类的实例（byType）
     */
    @Autowired
    //@Qualifier("singleHelloWorldAnnotationService")
    private HelloWorldServiceBaseAnnotation helloWorldServiceBaseAnnotation;

    public void sayHello(){
        helloWorldServiceBaseAnnotation.sayHello();
    }
}
