package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.core.extension.Extensible;

/**
 * 异步线程池工厂标记接口
 * {@link Async#executorName()} 中填写工厂中对应的ExecutorService的方法名。
 * 多个工厂有同名方法时，只会取其中一个。
 * 生成ExecutorService的方法必须无参
 *
 * @author icodening
 * @date 2021.01.23
 */
@Extensible
public interface AsyncExecutorFactory {
}
