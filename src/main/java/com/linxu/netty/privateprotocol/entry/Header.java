package com.linxu.netty.privateprotocol.entry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linxu
 * @date 2020/2/16
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public final class Header implements Serializable {
    /**
     * crc
     */
    private int crcCode = 0xabef0101;
    /**
     * length
     */
    private int length;
    /**
     * sessionId
     */
    private long sessionId;
    /**
     * 消息类型
     */
    private byte type;
    /**
     * 优先级
     */
    private byte priority;
    /**
     * 附件
     */
    private Map<String, Object> attachment = new HashMap<>();

    public Header(Builder builder) {
        setCrcCode(builder.crcCode);
        setLength(builder.length);
        setPriority(builder.priority);
        setSessionId(builder.sessionId);
        setType(builder.type);
    }

    public final int getCrcCode() {
        return crcCode;
    }

    public final int getLength() {
        return length;
    }

    public final long getSessionId() {
        return sessionId;
    }

    public final byte getType() {
        return type;
    }

    public final byte getPriority() {
        return priority;
    }

    public final Map<String, Object> getAttachment() {
        return attachment;
    }

    public final void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public final void setLength(int length) {
        this.length = length;
    }

    public final void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public final void setType(byte type) {
        this.type = type;
    }

    public final void setPriority(byte priority) {
        this.priority = priority;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder {
        /**
         * crc
         */
        private int crcCode = 0xabef0101;
        /**
         * length
         */
        private int length;
        /**
         * sessionId
         */
        private long sessionId;
        /**
         * 消息类型
         */
        private byte type;
        /**
         * 优先级
         */
        private byte priority;
        /**
         * 附件
         */
        private Map<String, Object> attachment = new HashMap<>();

        public Builder CrcCode(int crcCode) {
            this.crcCode = crcCode;
            return this;
        }

        public Builder Length(int length) {
            this.length = length;
            return this;
        }

        public Builder SessionId(long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder Type(byte type) {
            this.type = type;
            return this;
        }

        public Builder Priority(byte priority) {
            this.priority = priority;
            return this;
        }

        public Builder Attachment(Map<String, Object> attachment) {
            this.attachment = attachment;
            return this;
        }

        public Header build() {
            return new Header(this);
        }
    }


    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
