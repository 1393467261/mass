package com.hzw.mass.entity;

/**
 * Copyright@www.localhost.com.
 * Author:H.zw
 * Date:2018/4/3 16:29
 * Description:
 */
public class AccessToken {

    private String access_token;
    private String expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public AccessToken() {

    }
}
