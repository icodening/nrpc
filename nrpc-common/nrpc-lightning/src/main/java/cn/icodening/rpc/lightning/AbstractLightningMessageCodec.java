package cn.icodening.rpc.lightning;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.Codec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.util.Bytes;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.20
 */
public abstract class AbstractLightningMessageCodec<I extends ExchangeMessage, O extends ExchangeMessage> implements Codec<I, O> {

    protected final static int DATA_LENGTH = 4;

    protected final static int REQUEST_ID_LENGTH = 8;

    protected void encodeObject(Serialization serialization, NrpcBuffer buffer, Object data) {
        byte[] dataLengthBytes = new byte[DATA_LENGTH];
        try {
            ByteBuffer objectBuffer = serialization.serialize(data);
            int length = objectBuffer.limit();
            Bytes.int2bytes(length, dataLengthBytes);
            buffer.writeBytes(dataLengthBytes);
            byte[] bytes = new byte[length];
            objectBuffer.get(bytes);
            buffer.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NrpcException("序列化失败, " + serialization);
        }
    }

    protected <T> T decodeObject(Serialization serialization, NrpcBuffer buffer, int length, Class<T> clazz) {
        try {
            T result = null;
            byte[] dataLengthBytes = new byte[length];
            buffer.readBytes(dataLengthBytes);
            int dataLength = Bytes.bytes2int(dataLengthBytes);
            if (dataLength > 0) {
                byte[] dataBytes = new byte[dataLength];
                buffer.readBytes(dataBytes);
                ByteBuffer wrap = ByteBuffer.wrap(dataBytes);
                result = serialization.deserialize(wrap, clazz);
                wrap = null;
            } else {
                ByteBuffer wrap = ByteBuffer.wrap(new byte[0]);
                result = serialization.deserialize(wrap, clazz);
            }
            return result;
        } catch (IOException e) {
            throw new NrpcException("反序列化错误");
        }
    }

    @Override
    public void encode(Serialization serialization, O response, NrpcBuffer buffer) {
        long id = getRequestId(response);
        byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
        Bytes.long2bytes(id, requestIdBytes);
        buffer.writeBytes(requestIdBytes);

        //2.编码headers
        NrpcHeaders headers = response.getHeaders();
        encodeObject(serialization, buffer, headers);

        //3.编码data
        Object data = response.getData();
        encodeObject(serialization, buffer, data);
    }

    protected abstract long getRequestId(O out);
}
