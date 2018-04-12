package com.hzw.mass.service;

import java.io.Serializable;

/**
 * @Author: Hzw
 * @Time: 2018/4/12 17:54
 * @Description:
 */
public class TextText implements Serializable {

    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TextText(String content) {
        this.content = content;
    }
}
