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

    public ErrorTypeCollect(Integer name, Integer y) {
        this.name = name;
        this.y = y;
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

    @Override
    public String toString() {
        return "ErrorTypeCollect{" +
                "name=" + name +
                ", y=" + y +
                '}';
    }
}
