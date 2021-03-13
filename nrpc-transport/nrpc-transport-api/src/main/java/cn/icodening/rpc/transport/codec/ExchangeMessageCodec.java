package cn.icodening.rpc.transport.codec;

import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.util.Bytes;
import cn.icodening.rpc.transport.serialization.Serialization;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class ExchangeMessageCodec implements Codec {

    private final static int DATA_LENGTH = 4;
    private final static int REQUEST_ID_LENGTH = 8;

    @Override
    public void encode(Serialization serialization, ExchangeMessage message, ByteBuffer buffer) {
        doEncode(serialization, message, buffer);
    }

    protected void doEncode(Serialization serialization, ExchangeMessage message, ByteBuffer buffer) {
        //1.编码请求id
        long id = message.getId();
        byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
        Bytes.long2bytes(id, requestIdBytes);
        buffer.put(requestIdBytes);

        //2.编码headers
        NrpcHeaders headers = message.getHeaders();
        encodeObject(serialization, buffer, headers);

        //3.编码data
        Object data = message.getData();
        encodeObject(serialization, buffer, data);
    }

//    protected void doEncodeResponse(Serialization serialization, Response response, ByteBuffer buffer) {
//        //1.编码请求id
//        byte[] requestIdLengthBytes = new byte[8];
//        buffer.get(requestIdLengthBytes, 0, 8);
//        long requestId = response.getRequestId();
//        Bytes.long2bytes(requestId, requestIdLengthBytes);
//        buffer.put(requestIdLengthBytes);
//        //2.编码响应头
//        NrpcHeaders headers = response.getHeaders();
//        encodeObject(serialization, buffer, headers);
//
//        //3.编码结果
//        Object result = response.getResult();
//        encodeObject(serialization, buffer, result);
//    }
//
//    protected void doEncodeRequest(Serialization serialization, Request message, ByteBuffer buffer) {
//        //1.编码请求id
//        long id = message.getId();
//        byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
//        Bytes.long2bytes(id, requestIdBytes);
//        buffer.put(requestIdBytes);
//
//        //2.编码headers
//        NrpcHeaders headers = message.getHeaders();
//        encodeObject(serialization, buffer, headers);
//
//        //3.编码data
//        Object data = message.getData();
//        encodeObject(serialization, buffer, data);
//    }

    protected void encodeObject(Serialization serialization, ByteBuffer buffer, Object data) {
        byte[] dataLengthBytes = new byte[DATA_LENGTH];
//        buffer.put(dataLengthBytes);
        try {
//            buffer.mark();
//            int p = buffer.position();
            ByteBuffer objectBuffer = serialization.serialize(data);
            int length = objectBuffer.limit();
            Bytes.int2bytes(length, dataLengthBytes);
            buffer.put(dataLengthBytes);
            buffer.put(objectBuffer);
//            buffer.put(serialize);
////            serialization.serialize(data, buffer);
//            int length = buffer.position() - p;
//            if (length == 0) {
//                buffer.reset();
//                return;
//            }
//            int newPosition = buffer.position();
//            buffer.position(p - DATA_LENGTH);
//            buffer.put(dataLengthBytes);
//            buffer.position(newPosition);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NrpcException("序列化失败, " + serialization);
        }
    }


    @Override
    public void decode(Serialization serialization, ByteBuffer buffer, ExchangeMessage message) {
        doDecode(serialization, buffer, message);
        if (message instanceof Response) {
            ((Response) message).setRequestId(message.getId());
        }
//        if (message instanceof Request) {
//            doDecodeRequest(serialization, buffer, (Request) message);
//        } else if (message instanceof Response) {
//            doDecodeResponse(serialization, buffer, (Response) message);
//        }
    }

    protected void doDecode(Serialization serialization, ByteBuffer buffer, ExchangeMessage message) {
        byte[] requestIdLengthBytes = new byte[REQUEST_ID_LENGTH];
        buffer.get(requestIdLengthBytes, 0, REQUEST_ID_LENGTH);
        long requestId = Bytes.bytes2long(requestIdLengthBytes);
        message.setId(requestId);
        NrpcHeaders headers = decodeObject(serialization, buffer, DATA_LENGTH, NrpcHeaders.class);
        message.setHeaders(headers);
        Object data = decodeObject(serialization, buffer, DATA_LENGTH, Object.class);
        message.setData(data);
    }

//    protected void doDecodeResponse(Serialization serialization, ByteBuffer buffer, Response response) {
//        byte[] requestIdLengthBytes = new byte[REQUEST_ID_LENGTH];
//        buffer.get(requestIdLengthBytes, 0, REQUEST_ID_LENGTH);
//        long requestId = Bytes.bytes2long(requestIdLengthBytes);
//        response.setRequestId(requestId);
//
//
//        NrpcHeaders headers = decodeObject(serialization, buffer, DATA_LENGTH, NrpcHeaders.class);
//        response.setHeaders(headers);
//        Object result = decodeObject(serialization, buffer, DATA_LENGTH, Object.class);
//        response.setResult(result);
//    }

//    protected void doDecodeRequest(Serialization serialization, ByteBuffer buffer, Request request) {
//        //1.先读取前8位的requestId
//        byte[] requestIdLengthBytes = new byte[REQUEST_ID_LENGTH];
//        buffer.get(requestIdLengthBytes, 0, REQUEST_ID_LENGTH);
//        long requestId = Bytes.bytes2long(requestIdLengthBytes);
//        request.setId(requestId);
//
//        //2.读取请求头
//        NrpcHeaders headers = decodeObject(serialization, buffer, DATA_LENGTH, NrpcHeaders.class);
//        request.setHeaders(headers);
//        //3.读取请求data
//        Object data = decodeObject(serialization, buffer, DATA_LENGTH, Object.class);
//        request.setData(data);
//    }

    protected <T> T decodeObject(Serialization serialization, ByteBuffer buffer, int length, Class<T> clazz) {
        try {
            T result = null;
            byte[] dataLengthBytes = new byte[length];
            buffer.get(dataLengthBytes);
            int dataLength = Bytes.bytes2int(dataLengthBytes);
            if (dataLength > 0) {
                byte[] dataBytes = new byte[dataLength];
                buffer.get(dataBytes);
                ByteBuffer wrap = ByteBuffer.wrap(dataBytes);
                result = serialization.deserialize(wrap, clazz);
                wrap = null;
            }
            return result;
        } catch (IOException e) {
            throw new NrpcException("反序列化错误");
        }
    }
}
