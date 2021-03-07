package cn.icodening.rpc.plugin.lifecycle;

/**
 * @author icodening
 * @date 2021.01.30
 */
public interface NrpcShutdownCallable {

    /**
     * 收到停机指令后触发
     */
    void onShutdown();
}
