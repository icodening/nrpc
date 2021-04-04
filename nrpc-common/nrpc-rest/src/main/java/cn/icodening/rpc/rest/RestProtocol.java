package cn.icodening.rpc.rest;

import cn.icodening.rpc.common.AbstractProtocol;
import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.core.codec.ClientCodec;
import cn.icodening.rpc.core.codec.ServerCodec;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class RestProtocol extends AbstractProtocol implements Protocol {

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
