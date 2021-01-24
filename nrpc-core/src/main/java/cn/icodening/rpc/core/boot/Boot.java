package cn.icodening.rpc.core.boot;

import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.Lifecycle;
import cn.icodening.rpc.core.Sortable;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * 启动引导
 *
 * @author icodening
 * @date 2020.12.31
 */
@Extensible
public interface Boot extends Initializer, Lifecycle, Sortable {

    /**
     * 判断是否已启动
     *
     * @return true 已启动，false未启动
     */
    boolean isStarted();

    /**
     * 获取运行状态
     *
     * @return 当前引导状态
     */
    String getState();
}
