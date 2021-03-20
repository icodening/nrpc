package cn.icodening.rpc.common.codec;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;

/**
 * @author icodening
 * @date 2021.03.12
 */
public interface ServerCodec extends Codec<Request, Response> {

    @Override
    void encode(Serialization serialization, Response response, NrpcBuffer nrpcBuffer);

    @Override
    Request decode(Serialization serialization, NrpcBuffer buffer);
}
