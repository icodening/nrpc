package cn.icodening.rpc.lightning;

import cn.icodening.rpc.common.AbstractProtocol;
import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.core.codec.ClientCodec;
import cn.icodening.rpc.core.codec.ServerCodec;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class LightningProtocol extends AbstractProtocol implements Protocol {

    public static final int DEFAULT_PORT = 12580;
    public static final String PROTOCOL_NAME = "lightning";

    @Override
    public int defaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    public ClientCodec getClientCodec() {
        return new LightningClientMessageCodec();
    }

    @Override
    public ServerCodec getServerCodec() {
        return new LightningServerMessageCodec();
    }


}
