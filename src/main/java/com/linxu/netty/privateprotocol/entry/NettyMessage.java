package com.linxu.netty.privateprotocol.entry;

import java.io.Serializable;

/**
 * @author linxu
 * @date 2020/2/16
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public final class NettyMessage implements Serializable, Cloneable {
    private Header header;
    private Object body;

    public NettyMessage(Header header, Object body) {
        this.header = header;
        this.body = body;
    }

    /**
     * for builder
     *
     * @param builder
     */
    private NettyMessage(Builder builder) {
        this.header = builder.header;
        this.body = builder.body;
    }

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    /**
     * builder
     */
    private static class Builder {
        Header header;
        Object body;

        private Builder() {
        }

        Builder header(Header header) {
            this.header = header;
            return this;
        }

        Builder body(Object body) {
            this.body = body;
            return this;
        }

        NettyMessage build() {
            return new NettyMessage(this);
        }
    }
}
