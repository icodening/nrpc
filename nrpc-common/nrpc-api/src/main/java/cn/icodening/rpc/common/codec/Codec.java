package cn.icodening.rpc.common.codec;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.serialization.Serialization;

/**
 * @author icodening
 * @date 2021.03.20
 */
public interface Codec<I, O> {

    I decode(Serialization serialization, NrpcBuffer buffer);

    void encode(Serialization serialization, O out, NrpcBuffer nrpcBuffer);

}
