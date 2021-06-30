package wheel.ioc.service.xmlservice.impl;

import wheel.ioc.service.xmlservice.HelloWorldServiceBaseXml;

public class HelloWorldServiceBaseXmlImpl implements HelloWorldServiceBaseXml {

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
