package cn.icodening.rpc.core.exchange;

import cn.icodening.rpc.core.util.MultiValueMap;
import cn.icodening.rpc.core.util.SimpleMultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class StandardRequest extends AbstractMessage implements Request {
    private static final AtomicLong ID_ADDER = new AtomicLong();

    private final Map<String, Object> attributes;

    private MultiValueMap<String, String> parameters;

    public StandardRequest() {
        this.id = ID_ADDER.getAndIncrement();
        this.parameters = new SimpleMultiValueMap<>();
        this.attributes = new HashMap<>();
    }

    @Override
    public void addParameter(String name, String value) {
        parameters.add(name, value);
    }

    @Override
    public void setParameter(String name, List<String> values) {
        parameters.addAll(name, values);
    }

    @Override
    public List<String> getParameters(String name) {
        return parameters.get(name);
    }

    @Override
    public String getParameter(String name) {
        return parameters.getFirst(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
