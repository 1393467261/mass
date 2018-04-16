package com.hzw.mass.entity;

/**
 * Copyright@www.localhost.com.
 * Author:H.zw
 * Date:2018/4/10 20:45
 * Description:
 */
public class ErrorTypeCollect {

    private Integer name;
    private Integer y;
    private Integer messageId;

    public ErrorTypeCollect(Integer name, Integer y, Integer messageId) {
        this.name = name;
        this.y = y;
        this.messageId = messageId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getName() {

        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

}
