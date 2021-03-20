package cn.icodening.rpc.common.codec;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;

/**
 * @author icodening
 * @date 2021.03.20
 */
public interface ClientCodec extends Codec<Response, Request> {

    @Override
    Response decode(Serialization serialization, NrpcBuffer buffer);

    @Override
    void encode(Serialization serialization, Request request, NrpcBuffer nrpcBuffer);
}
