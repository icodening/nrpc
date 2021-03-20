package cn.icodening.rpc.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer buffer;

    public ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        int position = this.buffer.position();
        int capacity = this.buffer.capacity();
        if (position < capacity) {
            this.buffer.put((byte) b);
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(capacity << 1);
        buffer.put(this.buffer);
        this.buffer = buffer;
    }

    public ByteBuffer buffer() {
        return buffer;
    }
}
