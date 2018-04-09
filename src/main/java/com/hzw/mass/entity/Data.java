package com.hzw.mass.entity;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable{

    private List<String> openid;

    public List<String> getOpenid() {
        return openid;
    }

    public void setOpenid(List<String> openid) {
        this.openid = openid;
    }

    public Data() {
    }

    @Override
    public String toString() {
        return "Data{" +
                "openid=" + openid +
                '}';
    }
}
