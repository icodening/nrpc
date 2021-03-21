package cn.icodening.rpc.core.exchange;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author icodening
 * @date 2021.03.10
 */
public abstract class AbstractMessage implements ExchangeMessage {
    private static final AtomicLong ID_INCREMENT = new AtomicLong();

    protected long id;

    protected NrpcHeaders headers;

    protected Object data;

    public AbstractMessage() {
        this.id = ID_INCREMENT.getAndIncrement();
        this.headers = new NrpcHeaders();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public NrpcHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void setHeaders(NrpcHeaders nrpcHeaders) {
        this.headers = nrpcHeaders;
    }

    @Override
    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }

    @Override
    public List<String> getHeaders(String key) {
        return headers.get(key);
    }

    @Override
    public String getHeader(String key) {
        return headers.getFirst(key);
    }

    @Override
    public void removeHeader(String key) {
        headers.remove(key);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

}
