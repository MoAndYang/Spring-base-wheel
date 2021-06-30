package wheel.ioc.service.annotationservice.impl;

import wheel.ioc.service.annotationservice.HelloWorldServiceBaseAnnotation;
import wheel.springframework.annotation.Component;
import wheel.springframework.annotation.Scope;
import wheel.springframework.annotation.Value;

//@Component(value = "singleHelloWorldAnnotationService")
@Component("singleHelloWorldAnnotationService")
@Scope
public class SingleHelloWorldServiceBaseAnnotationImpl implements HelloWorldServiceBaseAnnotation {

    @Value("hello annotation world, I am a singleton Instance")
    private String text;

    @Override
    public void sayHello() {
        System.out.println(this.text);
    }

    @Override
    public String getString() {
        return this.text;
    }
}
