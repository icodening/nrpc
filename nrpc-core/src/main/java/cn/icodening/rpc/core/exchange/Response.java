package cn.icodening.rpc.core.exchange;

/**
 * @author icodening
 * @date 2021.03.09
 */
public interface Response extends ExchangeMessage {

    long getRequestId();

    void setRequestId(long requestId);

    boolean isHeartbeat();

    int getCode();

    void setCode(int code);

    Object getResult();

    void setResult(Object result);

}
