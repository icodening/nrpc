package cn.icodening.rpc.rest;

import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.codec.ServerCodec;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class RestProtocol implements Protocol {

    public static final String PROTOCOL_NAME = "rest";

    @Override
    public int defaultPort() {
        return 80;
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public ClientCodec getClientCodec() {
        return new RestClientMessageCodec();
    }

    @Override
    public ServerCodec getServerCodec() {
        return new RestServerMessageCodec();
    }
}
