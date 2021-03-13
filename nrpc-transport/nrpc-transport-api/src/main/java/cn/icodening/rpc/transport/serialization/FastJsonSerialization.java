package cn.icodening.rpc.transport.serialization;

import cn.icodening.rpc.transport.ByteBufferInputStream;
import cn.icodening.rpc.transport.ByteBufferOutputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.11
 */
public class FastJsonSerialization implements Serialization {

    @Override
    public void serialize(Object object, ByteBuffer buffer) throws IOException {
        OutputStream out = new ByteBufferOutputStream(buffer);
        SerializeWriter serializeWriter = new SerializeWriter();
        JSONSerializer jsonSerializer = new JSONSerializer(serializeWriter);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        jsonSerializer.write(object);
        serializeWriter.writeTo(writer);
        serializeWriter.flush();
        serializeWriter.close();
        writer.println();
        writer.flush();
    }

    @Override
    public ByteBuffer serialize(Object object) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        ByteBufferOutputStream outputStream = new ByteBufferOutputStream(allocate);
        SerializeWriter serializeWriter = new SerializeWriter();
        JSONSerializer jsonSerializer = new JSONSerializer(serializeWriter);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
        jsonSerializer.write(object);
        serializeWriter.writeTo(writer);
        serializeWriter.flush();
        serializeWriter.close();
        writer.println();
        writer.flush();
        int limit = outputStream.buffer().flip().limit();
        byte[] data = new byte[limit];
        ByteBuffer wrap = ByteBuffer.wrap(data);
        wrap.put(allocate);
        wrap.position(0);
        return wrap;
    }

    @Override
    public <T> T deserialize(ByteBuffer buffer, Class<T> type) throws IOException {
        InputStream in = new ByteBufferInputStream(buffer);
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String jsonString = bufferedReader.readLine();
        return JSON.parseObject(jsonString, type);
    }
}
