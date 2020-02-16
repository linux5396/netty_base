package com.linxu.nio.aio;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author linxu
 * @date 2020/2/17
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class AioServer {
    public static void main(String[] args) {
        AioServerHandler aioServerHandler=new AioServerHandler(8090);
        new Thread(aioServerHandler).start();
    }
}
