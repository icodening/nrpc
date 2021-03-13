package cn.icodening.rpc.transport.codec;

import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.transport.serialization.Serialization;

import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.12
 */
public interface Codec {


    /**
     * 将请求or响应进行编码
     *
     * @param serialization 序列化方式
     * @param message       待编码消息
     * @param buffer        buffer
     */
    void encode(Serialization serialization, ExchangeMessage message, ByteBuffer buffer);

    /**
     * 将交换消息解码，请求or响应
     *
     * @param serialization 序列化方式
     * @param buffer        待解码消息
     * @param message       解码后的消息
     */
    void decode(Serialization serialization, ByteBuffer buffer, ExchangeMessage message);
}
