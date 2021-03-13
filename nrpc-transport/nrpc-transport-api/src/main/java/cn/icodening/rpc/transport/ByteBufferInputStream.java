package cn.icodening.rpc.transport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buffer;

    private int startIndex;

    private int length;

    private int position;

    private int limit;

    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
        this.startIndex = buffer.position();
        this.position = 0;
        this.limit = buffer.limit();

    }

    @Override
    public int read() throws IOException {
        if (position + 1 < limit) {
            return buffer.get(position++);
        }
        return -1;
    }
}
