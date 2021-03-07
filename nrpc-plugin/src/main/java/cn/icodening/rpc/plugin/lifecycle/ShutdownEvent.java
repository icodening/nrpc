package cn.icodening.rpc.plugin.lifecycle;

import cn.icodening.rpc.core.event.NrpcEvent;

/**
 * 虚拟机停止事件
 *
 * @author icodening
 * @date 2021.03.02
 */
public class ShutdownEvent extends NrpcEvent {

    public ShutdownEvent() {
        this("SHUTDOWN EVENT OBJECT");
    }

    public ShutdownEvent(Object object) {
        super(object);
    }
}
