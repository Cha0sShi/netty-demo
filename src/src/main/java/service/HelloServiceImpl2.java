package service;

public class HelloServiceImpl2 implements HelloService{
    @Override
    public String sayHello(String msg) {
        return "你好 ： " + msg;
    }
}
