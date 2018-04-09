package com.hzw.mass.entity;

import java.io.Serializable;

public class UserList implements Serializable{

    private Integer total;
    private Integer count;
    private String next_openid;
    private Data data;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {

        this.count = count;
    }

    public String getNext_openid() {
        return next_openid;
    }

    public void setNext_openid(String next_openid) {
        this.next_openid = next_openid;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserList{" +
                "total=" + total +
                ", count=" + count +
                ", next_openid='" + next_openid + '\'' +
                ", data=" + data +
                '}';
    }

    public UserList() {
    }
}
