package cn.icodening.rpc.core.exchange;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.09
 */
public interface Request extends ExchangeMessage {

    void addParameter(String name, String value);

    void setParameter(String name, List<String> values);

    List<String> getParameters(String name);

    String getParameter(String name);

    void setAttribute(String name, Object object);

    Object getAttribute(String name);

    void removeAttribute(String name);

}
