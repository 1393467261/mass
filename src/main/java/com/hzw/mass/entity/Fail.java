package com.hzw.mass.entity;

/**
 * Copyright@www.localhost.com.
 * Author:H.zw
 * Date:2018/4/3 18:02
 * Description:
 */
public class Fail {

    private String openId;
    private Integer errorCode;

    public Fail() {
    }

    public Fail(String openId, Integer errorCode) {
        this.openId = openId;
        this.errorCode = errorCode;
    }

    public String getOpenId() {

        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
