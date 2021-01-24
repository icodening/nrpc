package cn.icodening.rpc.core.boot;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author icodening
 * @date 2020.12.31
 */
abstract class AbstractBoot implements Boot {

    private static final String STARTED = "STARTED";
    private static final String NOT_STARTED = "NOT STARTED";

    private boolean bootState = false;

    private final AtomicBoolean atomicBootState = new AtomicBoolean(false);

    private final AtomicBoolean atomicInitState = new AtomicBoolean(false);

    @Override
    public final boolean isStarted() {
        return bootState;
    }

    @Override
    public String getState() {
        return bootState ? STARTED : NOT_STARTED;
    }

    @Override
    public final void initialize() {
        if (atomicInitState.compareAndSet(false, true)) {
            doInitialize();
        }
    }


    @Override
    public final void start() {
        if (atomicBootState.compareAndSet(false, true)) {
            doStart();
            bootState = true;
        }
    }

    @Override
    public final void destroy() {
        if (atomicBootState.compareAndSet(true, false)) {
            doDestroy();
            bootState = false;
        }
    }

    protected abstract void doInitialize();

    protected abstract void doStart();

    protected abstract void doDestroy();

}
