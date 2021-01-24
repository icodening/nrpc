package cn.icodening.rpc.core.extension;

import cn.icodening.rpc.core.Initializer;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2021.01.24
 */
@Extension(scope = Scope.PROTOTYPE)
public class TestExtensionImpl implements TestExtension {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    private final int id;

    public TestExtensionImpl() {
        this.id = ATOMIC_INTEGER.getAndIncrement();
    }

    @Override
    public void say(String string) {
        try {
            Random random = new Random();
            int i = random.nextInt(500);
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(id + ": " + string);
    }
}
