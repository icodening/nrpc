package cn.icodening.rpc.plugin;

import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.plugin.time.PrintTime;

import javax.annotation.PreDestroy;

/**
 * @author icodening
 * @date 2021.01.22
 */
public class BootTestImpl extends AbstractBootAdapter {

    @Override
    @PrintTime
    protected void doStart() {
        System.out.println("BootImpl 启动了");
    }

    @Override
    protected void doInitialize() {
        System.out.println("BootImpl 初始化 500ms");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void myDestroy(){
        System.out.println(this.getClass().getSimpleName() +" myDestroy 被销毁了");
    }

    @Override
    protected void doDestroy() {
        System.out.println(this.getClass().getSimpleName() +"被销毁了");
    }
}
