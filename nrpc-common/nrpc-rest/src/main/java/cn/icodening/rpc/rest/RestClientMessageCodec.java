package cn.icodening.rpc.rest;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardResponse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class RestClientMessageCodec implements ClientCodec {

    private final byte[] rn = new byte[]{'\r', '\n'};

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
            System.out.print((char) currentByte);
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
    public Response decode(Serialization serialization, NrpcBuffer buffer) {
        Response response = new StandardResponse();
        //读第一行
        int i = buffer.readerIndex();
        byte[] topLineBytes = readTopLine(buffer);
        byte[] headerBytes = readHeaders(buffer);
        NrpcHeaders headers = new NrpcHeaders();
        StringReader stringReader = new StringReader(new String(headerBytes));
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
            response.setHeaders(headers);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getHeader(RestConstants.CONTENT_LENGTH) == null) {
            return response;
        }
        int contentLength = Integer.parseInt(response.getHeader(RestConstants.CONTENT_LENGTH));
        if (contentLength < 1) {
            return response;
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
            response.setResult(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void encode(Serialization serialization, Request request, NrpcBuffer nrpcBuffer) {
        //写入第一行
        String method = request.getAttribute("method", RestConstants.POST, String.class);
        String uri = request.getAttribute("uri", "/", String.class);
        String protocol = request.getAttribute("protocol", RestConstants.PROTOCOL, String.class);
        String one = method + " " + uri + " " + protocol;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(one.getBytes());
            bos.write(rn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NrpcHeaders headers = request.getHeaders();
        Object data = request.getData();
        try {
            ByteBuffer dataBuffer = serialization.serialize(data);
            int length = dataBuffer.limit();
            headers.set(RestConstants.CONTENT_LENGTH, String.valueOf(length));
            headers.set(RestConstants.CONTENT_TYPE, RestConstants.CONTENT_TYPE_APPLICATION_JSON);
            headers.forEach((key, values) -> {
                StringBuilder sb = new StringBuilder(key);
                sb.append(":").append(" ");
                for (String value : values) {
                    sb.append(value).append(",");
                }
                try {
                    bos.write(sb.substring(0, sb.length() - 1).getBytes());
                    bos.write(rn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bos.write(rn);
            byte[] bytes = new byte[length];
            dataBuffer.get(bytes);
            bos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nrpcBuffer.writeBytes(bos.toByteArray());
    }
}
