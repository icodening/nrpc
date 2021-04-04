package cn.icodening.nrpc.example;

import cn.icodening.nrpc.example.api.IHelloService;

/**
 * @author icodening
 * @date 2021.03.21
 */
public class HelloService implements IHelloService {

    @Override
    public void say(String hello) {
        System.out.println("nrpc provider say: " + hello);
    }
}
