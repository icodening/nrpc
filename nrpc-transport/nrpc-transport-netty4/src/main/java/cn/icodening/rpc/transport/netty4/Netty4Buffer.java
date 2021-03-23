package cn.icodening.rpc.transport.netty4;

import cn.icodening.rpc.common.NrpcBuffer;
import cn.icodening.rpc.common.NrpcBufferAdapter;
import io.netty.buffer.ByteBuf;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class Netty4Buffer extends NrpcBufferAdapter {

    private final ByteBuf byteBuf;

    public Netty4Buffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public void readBytes(byte[] bytes) {
        byteBuf.readBytes(bytes);
    }

    @Override
    public byte getByte(int index) {
        return byteBuf.getByte(index);
    }

    @Override
    public void get(byte[] bytes) {
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
    }

    @Override
    public void get(int offset, byte[] bytes) {
        byteBuf.getBytes(offset, bytes);
    }

    @Override
    public int readableBytes() {
        return byteBuf.readableBytes();
    }

    @Override
    public void writeBytes(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    @Override
    public void writeBytes(NrpcBuffer buffer) {
        int readableBytes = buffer.readableBytes();
        byte[] data = new byte[readableBytes];
        buffer.readBytes(data);
        byteBuf.writeBytes(data);
    }

    @Override
    public void readerIndex(int index) {
        byteBuf.readerIndex(index);
    }

    @Override
    public int readerIndex() {
        return byteBuf.readerIndex();
    }

    @Override
    public void markReaderIndex() {
        byteBuf.markReaderIndex();
    }

    @Override
    public void resetReaderIndex() {
        byteBuf.resetReaderIndex();
    }

    @Override
    public boolean isReadable() {
        return byteBuf.isReadable();
    }

    @Override
    public boolean isWriteable() {
        return byteBuf.isWritable();
    }
}
