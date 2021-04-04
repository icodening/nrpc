package cn.icodening.rpc.config.impl;

import cn.icodening.rpc.config.api.IHelloService;

/**
 * @author icodening
 * @date 2021.03.16
 */
public class HelloServiceImpl implements IHelloService {
    @Override
    public void say(String hello) {
        System.out.println("rpc: " + hello);
    }
}
