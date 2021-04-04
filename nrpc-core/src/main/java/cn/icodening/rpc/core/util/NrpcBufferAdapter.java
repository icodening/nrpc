package cn.icodening.rpc.core.util;

/**
 * @author icodening
 * @date 2021.03.20
 */
public abstract class NrpcBufferAdapter implements NrpcBuffer {

    @Override
    public void markReaderIndex() {

    }

    @Override
    public void resetReaderIndex() {

    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWriteable() {
        return false;
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public void readBytes(byte[] bytes) {

    }

    @Override
    public byte getByte(int index) {
        return 0;
    }

    @Override
    public void get(byte[] bytes) {

    }

    @Override
    public void get(int offset, byte[] bytes) {

    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public void writeBytes(byte[] bytes) {

    }

    @Override
    public void writeBytes(NrpcBuffer buffer) {

    }

    @Override
    public void readerIndex(int index) {

    }

    @Override
    public int readerIndex() {
        return 0;
    }
}
