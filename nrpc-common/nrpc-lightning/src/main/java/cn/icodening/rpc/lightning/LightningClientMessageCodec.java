package cn.icodening.rpc.lightning;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardResponse;
import cn.icodening.rpc.core.util.Bytes;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class LightningClientMessageCodec extends AbstractLightningMessageCodec<Response, Request> implements ClientCodec {

    @Override
    public Response decode(Serialization serialization, NrpcBuffer buffer) {
        //将byte解码成Response
        if (buffer.readableBytes() < REQUEST_ID_LENGTH) {
            return null;
        }
        Response response = new StandardResponse();
        byte[] requestIdLengthBytes = new byte[REQUEST_ID_LENGTH];
        buffer.readBytes(requestIdLengthBytes);
        long requestId = Bytes.bytes2long(requestIdLengthBytes);
        response.setRequestId(requestId);
        if (buffer.readableBytes() < DATA_LENGTH) {
            return null;
        }
        byte[] headerLengthBytes = new byte[DATA_LENGTH];
        buffer.get(headerLengthBytes);
        int headerLength = Bytes.bytes2int(headerLengthBytes);
        if (headerLength > buffer.readableBytes()) {
            return null;
        }
        NrpcHeaders headers = decodeObject(serialization, buffer, DATA_LENGTH, NrpcHeaders.class);
        response.setHeaders(headers);

        byte[] dataLengthBytes = new byte[DATA_LENGTH];
        buffer.get(dataLengthBytes);
        int dataLength = Bytes.bytes2int(dataLengthBytes);
        if (dataLength > buffer.readableBytes()) {
            return null;
        }
        Object data = decodeObject(serialization, buffer, DATA_LENGTH, Object.class);
        response.setData(data);
        return response;
    }

    @Override
    protected long getRequestId(Request out) {
        return out.getId();
    }

}
