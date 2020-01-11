package com.linxu.netty;

/**
 * @author linxu
 * @date 2019/9/8
 * <tip>take care of yourself.everything is no in vain.</tip>
 * 抽象启动器
 */
public abstract class AbstractStarter {
    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;
}
