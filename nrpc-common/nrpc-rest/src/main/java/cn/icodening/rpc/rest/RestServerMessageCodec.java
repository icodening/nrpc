package cn.icodening.rpc.rest;

import cn.icodening.rpc.core.codec.ServerCodec;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardRequest;
import cn.icodening.rpc.core.serialization.Serialization;
import cn.icodening.rpc.core.util.NrpcBuffer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class RestServerMessageCodec implements ServerCodec {
    @Override
    public void encode(Serialization serialization, Response response, NrpcBuffer nrpcBuffer) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        NrpcHeaders headers = response.getHeaders();
        if (headers == null) {
            headers = new NrpcHeaders();
            response.setHeaders(headers);
        }
        String protocol = headers.getFirst("protocol", "HTTP/1.1");
        String code = headers.getFirst("code", "200");
        String codeDesc = headers.getFirst("codeDesc", "OK");
        String one = protocol + " " + code + " " + codeDesc;
        try {
            bos.write(one.getBytes());
            bos.write(new byte[]{'\r', '\n'});
        } catch (IOException e) {
            e.printStackTrace();
        }
        headers.remove("protocol");
        headers.remove("code");
        headers.remove("codeDesc");
        try {
            ByteBuffer dataBuffer = serialization.serialize(response.getData());
            int contentLength = dataBuffer.limit();
            byte[] dataBytes = new byte[contentLength];
            dataBuffer.get(dataBytes);
            headers.set(RestConstants.CONTENT_LENGTH, String.valueOf(contentLength));
            headers.set(RestConstants.CONTENT_TYPE, RestConstants.CONTENT_TYPE_APPLICATION_JSON);

            response.getHeaders().forEach((key, values) -> {
                StringBuilder valuesBuilder = new StringBuilder(key).append(":").append(" ");
                for (String value : values) {
                    valuesBuilder.append(value).append(",");
                }
                try {
                    bos.write(valuesBuilder.substring(0, valuesBuilder.length() - 1).getBytes());
                    bos.write(new byte[]{'\r', '\n'});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bos.write(new byte[]{'\r', '\n'});
            bos.write(dataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nrpcBuffer.writeBytes(bos.toByteArray());
    }

    protected byte[] readTopLine(NrpcBuffer buffer) {
        int newIndex = buffer.readerIndex();
        buffer.markReaderIndex();
        while (true) {
            byte currentByte = buffer.readByte();
            newIndex++;
            if (currentByte == '\r' && buffer.readByte() == '\n') {
                buffer.resetReaderIndex();
                newIndex++;
                break;
            }
        }
        int oldIndex = buffer.readerIndex();
        byte[] bytes = new byte[newIndex - oldIndex];
        buffer.readBytes(bytes);
        return bytes;
    }

    protected byte[] readHeaders(NrpcBuffer buffer) {
        buffer.markReaderIndex();
        int length = 0;
        while (true) {
            byte currentByte = buffer.readByte();
            length++;
            if (currentByte == '\r'
                    && buffer.getByte(buffer.readerIndex() - 2) == '\n'
                    && buffer.readByte() == '\n'
            ) {
                buffer.resetReaderIndex();
                length++;
                break;
            }
        }
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    @Override
    public Request decode(Serialization serialization, NrpcBuffer buffer) {
        Request request = new StandardRequest();
        NrpcHeaders headers = new NrpcHeaders();
        int i = buffer.readerIndex();
        //读第一行
        if (!buffer.isReadable()) {
            return null;
        }
        byte[] topLineBytes = readTopLine(buffer);
        String topLine = new String(topLineBytes);
        String[] s = topLine.split(" ");
        headers.set("method", s[0]);
        headers.set("uri", s[1]);
        headers.set("protocol", s[2]);
        if (!buffer.isReadable()) {
            return null;
        }
        byte[] headersBytes = readHeaders(buffer);
        String header = new String(headersBytes);
        StringReader stringReader = new StringReader(header);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if ("".equals(line)) {
                    continue;
                }
                int index = line.indexOf(":");
                String name = line.substring(0, index);
                String value = line.substring(index + 1);
                String trim = value.trim();
                headers.set(name, trim);
            }
            request.setHeaders(headers);
            if (headers.getFirst("request-id") != null) {
                request.setId(Long.parseLong(headers.getFirst("request-id")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (request.getHeader(RestConstants.CONTENT_LENGTH) == null) {
            return request;
        }
        int contentLength = Integer.parseInt(request.getHeader(RestConstants.CONTENT_LENGTH));
        if (contentLength < 1) {
            return request;
        }
        if (contentLength > buffer.readableBytes()) {
            buffer.readerIndex(i);
            return null;
        }
        byte[] dataBytes = new byte[contentLength];
        buffer.readBytes(dataBytes);
        ByteBuffer wrap = ByteBuffer.wrap(dataBytes);
        Object data = null;
        try {
            data = serialization.deserialize(wrap, Object.class);
            request.setData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }
}
