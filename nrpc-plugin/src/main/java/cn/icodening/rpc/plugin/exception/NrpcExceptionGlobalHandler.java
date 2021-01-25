package cn.icodening.rpc.plugin.exception;

import cn.icodening.rpc.aop.AfterThrowingAdvice;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.util.ExceptionI18nUtil;
import org.apache.log4j.Logger;


/**
 * @author icodening
 * @date 2021.01.25
 */
public class NrpcExceptionGlobalHandler implements AfterThrowingAdvice {

    private static final Logger LOGGER = Logger.getLogger(NrpcExceptionGlobalHandler.class);

    public void afterThrowing(NrpcException nrpcException) {
        String message = nrpcException.getMessage();
        String i18nMessage = ExceptionI18nUtil.get(message);
        if (i18nMessage != null) {
            message = i18nMessage;
        }
        LOGGER.error(message);
    }
}
