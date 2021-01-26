package cn.icodening.rpc.plugin.exception;

import cn.icodening.rpc.aop.AfterThrowingAdvice;
import cn.icodening.rpc.core.NrpcException;
import org.apache.log4j.Logger;


/**
 * @author icodening
 * @date 2021.01.25
 */
public class NrpcExceptionGlobalHandler implements AfterThrowingAdvice {

    private static final Logger LOGGER = Logger.getLogger(NrpcExceptionGlobalHandler.class);

    public void afterThrowing(NrpcException nrpcException) {
        LOGGER.error(nrpcException.getMessage());
    }
}
