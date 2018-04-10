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
    private Integer summaryId;

    public Fail(String openId, Integer errorCode, Integer summaryId) {
        this.openId = openId;
        this.errorCode = errorCode;
        this.summaryId = summaryId;
    }

    public Fail(String openId, Integer errcode) {
        this.openId = openId;
        this.errorCode = errcode;
    }

    public Integer getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Integer summaryId) {
        this.summaryId = summaryId;
    }

    public Fail() {
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

    @Override
    public String toString() {
        return "Fail{" +
                "openId='" + openId + '\'' +
                ", errorCode=" + errorCode +
                ", summaryId=" + summaryId +
                '}';
    }
}
