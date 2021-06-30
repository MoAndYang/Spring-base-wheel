package wheel.ioc.service.annotationservice.impl;

import wheel.ioc.service.annotationservice.HelloWorldServiceBaseAnnotation;
import wheel.springframework.annotation.Component;
import wheel.springframework.annotation.Scope;
import wheel.springframework.annotation.Value;

//@Component(value = "protoHelloWorldService")
//@Scope(value = "prototype")
@Component("protoHelloWorldAnnotationService")
@Scope("prototype")
public class ProtoHelloWorldServiceBaseAnnotationImpl implements HelloWorldServiceBaseAnnotation {

    @Value("hello annotation world, I am a prototype Instance")
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
