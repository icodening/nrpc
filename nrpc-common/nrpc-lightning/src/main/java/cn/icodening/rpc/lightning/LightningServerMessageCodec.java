package cn.icodening.rpc.lightning;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardRequest;
import cn.icodening.rpc.core.util.Bytes;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class LightningServerMessageCodec extends AbstractLightningMessageCodec<Request, Response> implements ServerCodec {

    @Override
    protected long getRequestId(Response out) {
        return out.getRequestId();
    }

    @Override
    public Request decode(Serialization serialization, NrpcBuffer buffer) {
        int readerIndex = buffer.readerIndex();
        if (buffer.readableBytes() < REQUEST_ID_LENGTH) {
            return null;
        }
        //读请求id
        byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
        buffer.readBytes(requestIdBytes);
        long requestId = Bytes.bytes2long(requestIdBytes);
        Request request = new StandardRequest();
        request.setId(requestId);
        if (buffer.readableBytes() < DATA_LENGTH) {
            buffer.readerIndex(readerIndex);
            return null;
        }

        //读header
        byte[] headerLengthBytes = new byte[DATA_LENGTH];
        buffer.get(headerLengthBytes);
        int headerLength = Bytes.bytes2int(headerLengthBytes);
        if (headerLength > buffer.readableBytes()) {
            buffer.readerIndex(readerIndex);
            return null;
        }
        NrpcHeaders headers = decodeObject(serialization, buffer, DATA_LENGTH, NrpcHeaders.class);
        request.setHeaders(headers);

        //读data
        byte[] dataLengthBytes = new byte[DATA_LENGTH];
        buffer.get(dataLengthBytes);
        int dataLength = Bytes.bytes2int(dataLengthBytes);
        if (dataLength > buffer.readableBytes()) {
            buffer.readerIndex(readerIndex);
            return null;
        }
        Object data = decodeObject(serialization, buffer, DATA_LENGTH, Object.class);
        request.setData(data);
        return request;
    }

}
