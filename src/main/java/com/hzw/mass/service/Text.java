package com.hzw.mass.service;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @Author: Hzw
 * @Time: 2018/4/12 17:50
 * @Description:
 */
public class Text extends OutputStream implements Serializable{

    String touser;

    String msgtype = "text";

    TextText text;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextText getContent() {
        return text;
    }

    public void setContent(TextText content) {
        this.text = content;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public void write(int b) throws IOException {

    }
}
