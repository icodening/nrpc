package cn.icodening.rpc.rest;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.serialization.Serialization;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardResponse;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class RestClientMessageCodec implements ClientCodec {

    @Override
    public Response decode(Serialization serialization, NrpcBuffer buffer) {
        Response response = new StandardResponse();
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
                response.setCode(Integer.parseInt(split[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        response.setHeaders(headers);
        ByteBuffer wrap = ByteBuffer.wrap(data);
        try {
            String deserialize = serialization.deserialize(wrap, String.class);
            response.setData(deserialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void encode(Serialization serialization, Request request, NrpcBuffer nrpcBuffer) {
        //写入第一行
        String method = request.getAttribute("method", "POST", String.class);
        String uri = request.getAttribute("uri", "/", String.class);
        String protocol = request.getAttribute("protocol", "HTTP/1.1", String.class);
        String one = method + " " + uri + " " + protocol;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        printWriter.println(one);
        NrpcHeaders headers = request.getHeaders();
        headers.forEach((key, values) -> {
            StringBuilder sb = new StringBuilder(key);
            sb.append(":");
            for (String value : values) {
                sb.append(value).append(",");
            }
            printWriter.print(sb.substring(0, sb.length() - 1));
        });
        printWriter.println();
        Object data = request.getData();
        try {
            ByteBuffer serialize = serialization.serialize(data);
            int length = serialize.limit();
            headers.set("Content-Length", String.valueOf(length));
            byte[] bytes = new byte[length];
            serialize.get(bytes);
            printWriter.println(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nrpcBuffer.writeBytes(byteArrayOutputStream.toByteArray());
    }
}
