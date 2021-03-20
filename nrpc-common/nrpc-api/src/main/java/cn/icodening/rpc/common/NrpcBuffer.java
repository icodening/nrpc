package cn.icodening.rpc.common;

/**
 * @author icodening
 * @date 2021.03.20
 */
public interface NrpcBuffer {

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
