package cn.icodening.rpc.core;

/**
 * @author icodening
 * @date 2021.01.25
 */
public class NrpcException extends RuntimeException {

    public NrpcException(String codeOrMessage) {
        super(codeOrMessage);
    }
}
