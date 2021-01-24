# nrpc
## 项目介绍
nRPC是一个简单轻量的开源RPC框架，包含常见的RPC功能。  
如：服务注册、服务发现、负载均衡、流量控制、服务降级、服务熔断等功能。  
该开源项目仅供学习，请勿用于其他用途。  
如有不足可留下宝贵的意见一起学习。
## 项目模块
### nrpc-aop
AOP切面的实现，暂仅支持JDK动态代理的方式。
### nrpc-core
项目的基础核心模块，包含SPI机制的实现、常用工具类、基本接口定义、Service Provider的多种作用域实现等。
### nrpc-plugin
nrpc插件模块，该模块主要用于动态增强Service Provider的功能。  
目前默认基础实现如下：  
1. Service Provider 初始化功能
2. Boot 引导类实例自动销毁功能
3. 异步任务功能
4. 打印方法耗时功能
### nrpc-registry
nrpc的注册服务模块，主要用于服务的注册、反注册、订阅、反订阅等功能
### nrpc-transport
nrpc的传输模块，也是RPC的核心。该模块用于实现服务与服务之间的网络通讯

### 后续还会有其他模块加入......

## 学习参考实现  
Dubbo  
Spring  
