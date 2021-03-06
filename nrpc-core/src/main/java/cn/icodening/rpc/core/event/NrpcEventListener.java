package cn.icodening.rpc.core.event;

import cn.icodening.rpc.core.extension.Extensible;

import java.util.EventListener;

/**
 * @author icodening
 * @date 2021.02.06
 */
@Extensible
public interface NrpcEventListener<E extends Event> extends EventListener {

    /**
     * 事件回调
     *
     * @param event 事件源
     */
   void onEvent(E event);
}
