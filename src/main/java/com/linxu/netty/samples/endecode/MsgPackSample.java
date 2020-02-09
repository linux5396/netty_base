package com.linxu.netty.samples.endecode;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 * msg pack编解码器
 */
public class MsgPackSample {
    public static void main(String[] args) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add("this is:" + i);
        }
        MessagePack messagePack = new MessagePack();
        try {
            byte[] serialObj = messagePack.write(list);
            List<String> convertSerialObj = messagePack.read(serialObj, Templates.tList(Templates.TString));
            for (String s : convertSerialObj
                    ) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
