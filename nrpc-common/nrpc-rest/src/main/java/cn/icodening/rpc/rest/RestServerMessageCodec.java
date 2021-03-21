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
    public Request decode(Serialization serialization, NrpcBuffer buffer) {
        Request request = new StandardRequest();
        int i = buffer.readerIndex();
        //读第一行
        if (!buffer.isReadable()) {
            return null;
        }
        byte[] topLineBytes = readTopLine(buffer);
        if (!buffer.isReadable()) {
            return null;
        }
        byte[] headersBytes = readHeaders(buffer);
        String header = new String(headersBytes);
        NrpcHeaders headers = new NrpcHeaders();
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
//        int readableBytes = buffer.readableBytes();
//        byte[] bytes = new byte[readableBytes];
//        int index = 0;
//        while (true) {
//            byte b = buffer.getByte(index);
////            bytes[index] = b;
//            if (b == '\r' && buffer.getByte(index + 1) == '\n') {
//                int temp = index + 1;
//                index++;
////                bytes[index] = buffer.getByte(temp);
//                break;
//            }
//            index++;
//        }
//        buffer.readBytes(topLineBytes);
//        int oneIndex = index + 1;
//        String topLine = new String(topLineBytes, 0, index + 1);
//        StringReader one = new StringReader(topLine);
//        BufferedReader oneBufferedReader = new BufferedReader(one);
//        //读header
//        while (true) {
//            byte b = buffer.getByte(index);
//            bytes[index] = b;
//            if (b == '\r' && buffer.getByte(index + 1) == '\n'
//                    && buffer.getByte(index - 1) == '\n') {
//                int temp = index + 1;
//                ++index;
//                bytes[index] = buffer.getByte(temp);
//                break;
//            }
//            index++;
//        }
//        StringReader headerReader = new StringReader(new String(bytes, oneIndex, index - oneIndex + 1));
//        BufferedReader headerBufferReader = new BufferedReader(headerReader);
//        NrpcHeaders headers = new NrpcHeaders();
//        while (true) {
//            String header = null;
//            try {
//                header = headerBufferReader.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (header == null) {
//                break;
//            }
//            if ("".equals(header)) {
//                break;
//            }
//            if (!header.contains(":")) {
//                continue;
//            }
//            int splitIndex = header.indexOf(":");
//            String key = header.substring(0, splitIndex);
//            String value = header.substring(splitIndex + 2);
//            headers.set(key, value);
//        }
//        while (true) {
//            String[] split = new String[0];
//            try {
//                String line = oneBufferedReader.readLine();
//                if (line == null) {
//                    break;
//                }
//                split = line.split(" ");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            request.setAttribute("method", split[0]);
//            request.setAttribute("uri", split[1]);
//            request.setAttribute("protocol", split[2]);
//        }
//        //判断contentLength是否满足需求
//        String contentLengthString = headers.getFirst("content-length");
//        if (contentLengthString == null || Integer.parseInt(contentLengthString) < 0) {
//            return new StandardRequest();
//        }
//        int contentLength = Integer.parseInt(contentLengthString);
//        buffer.readerIndex(buffer.readerIndex() + index);
//        if (contentLength > buffer.readableBytes()) {
//            return null;
//        }
//        //跳过header之后的换行符
//        buffer.readerIndex(buffer.readerIndex() + 1);
//        byte[] data = new byte[contentLength];
//        buffer.readBytes(data);
//        request.setHeaders(headers);
//        ByteBuffer wrap = ByteBuffer.wrap(data);
//        try {
//            Object deserialize = serialization.deserialize(wrap, Object.class);
//            request.setData(deserialize);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return request;
    }
}
