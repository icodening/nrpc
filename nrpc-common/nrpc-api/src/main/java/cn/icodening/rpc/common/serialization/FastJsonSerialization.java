package cn.icodening.rpc.common.serialization;

import cn.icodening.rpc.common.ByteBufferOutputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.20
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
        byte[] bytes = JSON.toJSONBytes(object);
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public <T> T deserialize(ByteBuffer buffer, Class<T> type) throws IOException {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return JSON.parseObject(bytes, type);
    }
}
