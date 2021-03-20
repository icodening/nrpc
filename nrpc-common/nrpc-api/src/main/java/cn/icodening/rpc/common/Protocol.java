package cn.icodening.rpc.common;

import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.19
 */
@Extensible("lightning")
public interface Protocol {

    /**
     * 获取默认端口号
     *
     * @return 当前协议的默认端口
     */
    int defaultPort();

    ClientCodec getClientCodec();

    ServerCodec getServerCodec();
}
