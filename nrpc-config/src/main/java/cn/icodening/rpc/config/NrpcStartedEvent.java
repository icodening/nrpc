package cn.icodening.rpc.config;

import cn.icodening.rpc.core.event.Event;
import cn.icodening.rpc.core.event.NrpcEvent;

/**
 * @author icodening
 * @date 2021.03.14
 */
public class NrpcStartedEvent extends NrpcEvent implements Event {

    public NrpcStartedEvent(Object source) {
        super(source);
    }
}
