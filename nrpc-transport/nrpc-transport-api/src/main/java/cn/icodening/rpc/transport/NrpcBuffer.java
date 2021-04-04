package cn.icodening.rpc.transport;

/**
 * @author icodening
 * @date 2021.03.20
 */
public interface NrpcBuffer {

    void markReaderIndex();

    void resetReaderIndex();

    boolean isReadable();

    boolean isWriteable();

    byte readByte();

    void readBytes(byte[] bytes);

    byte getByte(int index);

    void get(byte[] bytes);

    void get(int offset, byte[] bytes);

    int readableBytes();

    void writeBytes(byte[] bytes);

    void writeBytes(NrpcBuffer buffer);

    void readerIndex(int index);

    int readerIndex();

}
