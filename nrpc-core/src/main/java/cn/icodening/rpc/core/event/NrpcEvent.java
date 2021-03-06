package cn.icodening.rpc.core.event;

import java.util.EventObject;

/**
 * @author icodening
 * @date 2021.03.06
 */
public abstract class NrpcEvent extends EventObject implements Event {

    private final long timestamp;

    public NrpcEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
