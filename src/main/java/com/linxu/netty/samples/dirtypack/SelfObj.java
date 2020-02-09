package com.linxu.netty.samples.dirtypack;

import org.msgpack.annotation.Message;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 * 该OBJ必须有公有的无参构造器
 */
@Message
public class SelfObj {
    private int id;
    private String description;
    public SelfObj(){

    }
    public SelfObj(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SelfObj{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
