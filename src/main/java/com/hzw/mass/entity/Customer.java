package com.hzw.mass.entity;

import org.omg.CORBA.INTERNAL;

/**
 * @Author: Hzw
 * @Time: 2018/4/12 15:54
 * @Description:
 */
public class Customer {

    private Integer subscribe;
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private Integer subscribe_time;
    private String unionid;
    private Integer groupid;
    private String remake;
    private String subscribe_scene;
    private Integer qr_scene;
    private String qr_scene_str;

    @Override
    public String toString() {
        return "Customer{" +
                "subscribe=" + subscribe +
                ", openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", language='" + language + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", subscribe_time=" + subscribe_time +
                ", unionid='" + unionid + '\'' +
                ", groupid=" + groupid +
                ", remake='" + remake + '\'' +
                ", subscribe_scene='" + subscribe_scene + '\'' +
                ", qr_scene=" + qr_scene +
                ", qr_scene_str='" + qr_scene_str + '\'' +
                '}';
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Integer getSubscribe_time() {
        return subscribe_time;
    }

    public void setSubscribe_time(Integer subscribe_time) {
        this.subscribe_time = subscribe_time;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

    public String getSubscribe_scene() {
        return subscribe_scene;
    }

    public void setSubscribe_scene(String subscribe_scene) {
        this.subscribe_scene = subscribe_scene;
    }

    public Integer getQr_scene() {
        return qr_scene;
    }

    public void setQr_scene(Integer qr_scene) {
        this.qr_scene = qr_scene;
    }

    public String getQr_scene_str() {
        return qr_scene_str;
    }

    public void setQr_scene_str(String qr_scene_str) {
        this.qr_scene_str = qr_scene_str;
    }
}
