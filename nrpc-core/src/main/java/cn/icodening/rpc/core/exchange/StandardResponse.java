package cn.icodening.rpc.core.exchange;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class StandardResponse extends AbstractMessage implements Response {

    private long requestId;

    private boolean heartbeat;

    private int code;

    private Object result;

    public StandardResponse() {
        this(false);
    }

    public StandardResponse(boolean heartbeat) {
        this(0, heartbeat);
    }

    public StandardResponse(long requestId, boolean heartbeat) {
        this.requestId = requestId;
        this.heartbeat = heartbeat;
    }


    @Override
    public long getRequestId() {
        return this.requestId;
    }

    @Override
    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean isHeartbeat() {
        return heartbeat;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
