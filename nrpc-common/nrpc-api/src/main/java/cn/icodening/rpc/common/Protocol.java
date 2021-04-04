package cn.icodening.rpc.common;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.codec.ClientCodec;
import cn.icodening.rpc.core.codec.ServerCodec;
import cn.icodening.rpc.core.extension.Extensible;
import cn.icodening.rpc.transport.Client;
import cn.icodening.rpc.transport.Server;

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

    String getProtocolName();

    ClientCodec getClientCodec();

    ServerCodec getServerCodec();

    Client refer(URL url);

    Server export(URL url);

}
