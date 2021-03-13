package cn.icodening.rpc.transport.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.11
 */
public interface Serialization {

    void serialize(Object object, ByteBuffer buffer) throws IOException;

    ByteBuffer serialize(Object object) throws IOException;

    <T> T deserialize(ByteBuffer buffer, Class<T> type) throws IOException;
}
