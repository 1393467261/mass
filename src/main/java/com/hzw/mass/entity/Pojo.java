package com.hzw.mass.entity;

import java.io.Serializable;

public class Pojo implements Serializable{

    private String openId;

    private Integer counts = 3;

    public void decr(){
        this.counts -= 1;
    }

    public Pojo(String openId) {
        this.openId = openId;
    }

    public Pojo() {

    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }
}
