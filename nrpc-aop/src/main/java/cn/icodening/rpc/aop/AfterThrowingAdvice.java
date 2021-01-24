package cn.icodening.rpc.aop;

/**
 * 抛出异常后置增强
 *
 * @author icodening
 * @date 2021.01.07
 */
public interface AfterThrowingAdvice extends ThrowsAdvice {

    String EXCEPTION_HANDLER_METHOD_NAME = "afterThrowing";
}
