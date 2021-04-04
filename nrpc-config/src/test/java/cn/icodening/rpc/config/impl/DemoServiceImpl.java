package cn.icodening.rpc.config.impl;

import cn.icodening.rpc.config.api.IDemoService;

/**
 * @author icodening
 * @date 2021.03.16
 */
public class DemoServiceImpl implements IDemoService {
    @Override
    public String demo
            (String hello) {
        return "demo-service response: " + hello;
    }
}
