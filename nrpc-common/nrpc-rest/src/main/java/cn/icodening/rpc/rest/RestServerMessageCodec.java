package cn.icodening.rpc.rest;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardRequest;

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
            headers.set("Content-Length", String.valueOf(contentLength));
            headers.set("Content-Type", "application/json");

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
            bos.write(new byte[]{'\r', '\n'});
        } catch (IOException e) {
            e.printStackTrace();
        }
        nrpcBuffer.writeBytes(bos.toByteArray());
    }

    @Override
    public Request decode(Serialization serialization, NrpcBuffer buffer) {
        Request request = new StandardRequest();
        //读第一行
        int readableBytes = buffer.readableBytes();
        byte[] bytes = new byte[readableBytes];
        int index = 0;
        while (true) {
            byte b = buffer.getByte(index);
            bytes[index++] = b;
            if (b == '\n') {
                break;
            }
        }
        StringReader one = new StringReader(new String(bytes, 0, index));
        BufferedReader oneBufferedReader = new BufferedReader(one);
        //读header
        while (true) {
            byte b = buffer.getByte(index);
            bytes[index++] = b;
            if (b == '\n') {
                //读完一行
                byte b1 = buffer.getByte(index);
                if (b1 == '\r') {
                    //读完header
                    break;
                }
            }
        }
        StringReader headerReader = new StringReader(new String(bytes, 0, index));
        BufferedReader headerBufferReader = new BufferedReader(headerReader);
        NrpcHeaders headers = new NrpcHeaders();
        while (true) {
            String header = null;
            try {
                header = headerBufferReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (header == null) {
                break;
            }
            if ("".equals(header)) {
                break;
            }
            if (!header.contains(":")) {
                continue;
            }
            int splitIndex = header.indexOf(":");
            String key = header.substring(0, splitIndex);
            String value = header.substring(splitIndex + 2);
            headers.set(key, value);
        }
        while (true) {
            String[] split = new String[0];
            try {
                String line = oneBufferedReader.readLine();
                if (line == null) {
                    break;
                }
                split = line.split(" ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.setAttribute("method", split[0]);
            request.setAttribute("uri", split[1]);
            request.setAttribute("protocol", split[2]);
        }
        //判断contentLength是否满足需求
        int contentLength = Integer.parseInt(headers.getFirst("content-length"));
        if (contentLength > readableBytes - (index - 1)) {
            return null;
        }
        //跳过header之后的换行符
        buffer.readerIndex(index + 2);
        byte[] data = new byte[contentLength];
        buffer.readBytes(data);
        request.setHeaders(headers);
        ByteBuffer wrap = ByteBuffer.wrap(data);
        try {
            Object deserialize = serialization.deserialize(wrap, Object.class);
            request.setData(deserialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }
}
