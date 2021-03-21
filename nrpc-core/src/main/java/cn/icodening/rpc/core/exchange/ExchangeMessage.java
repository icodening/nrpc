package cn.icodening.rpc.core.exchange;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.09
 */
public interface ExchangeMessage {

    long getId();

    void setId(long id);

    NrpcHeaders getHeaders();

    void setHeaders(NrpcHeaders nrpcHeaders);

    void addHeader(String name, String value);

    List<String> getHeaders(String key);

    String getHeader(String key);

    void removeHeader(String key);

    Object getData();

    void setData(Object data);
}
