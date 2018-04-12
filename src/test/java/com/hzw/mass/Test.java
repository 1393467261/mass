package com.hzw.mass;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hzw.mass.entity.*;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;

import javax.swing.plaf.metal.OceanTheme;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 16:26
 * @Description: 我的openId oRUz80_FTbCClMbeHJLD6oHUeAqE
 */
public class Test {

    public static void main(String[] args){

        String token = WxUtils.getAccessToken();
        String content = "hello";
        String message = WxUtils.makeTextCustomMessage("oRUz80_FTbCClMbeHJLD6oHUeAqE", content);

        ErrorMsg errorMsg = WxUtils.sendToUser(token, message);
        List<Fail> list = new ArrayList<>();
        list.add(new Fail("oRUz80_FTbCClMbeHJLD6oHUeAqE", errorMsg.getErrcode()));

        WxUtils.afterSend(JdbcUtil.getMaxId(), list);
    }

    @org.junit.Test
    public void test(){

        String token = WxUtils.getAccessToken();
        String openId = "oRUz80_FTbCClMbeHJLD6oHUeAqE";
        String uploadRespJson = UploadUtil.postFile(token, "C:\\Users\\13934\\Desktop\\liulei\\2.png");
        UploadResp uploadResp = new Gson().fromJson(uploadRespJson, UploadResp.class);
        System.out.println(uploadResp);
        String message = WxUtils.makePicAndTextMessage(openId, "美好的一天", "亲爱的，今天太阳好温暖啊", uploadResp.getUrl());
        ErrorMsg errorMsg = WxUtils.sendToUser(token, message);
        System.out.println(errorMsg);
    }

    @org.junit.Test
    public void test2(){

        App.ACCESS_TOKEN = WxUtils.getAccessToken();
        UserList openIds = WxUtils.getOpenIds(App.ACCESS_TOKEN);
        List<String> openidList = openIds.getData().getOpenid();
        List<Customer> customerInfoList = WxUtils.getCustomerInfoList(openidList);
        JdbcUtil.saveCustomerInfo2Db(customerInfoList);
    }

    @org.junit.Test
    public void test3(){
        String str = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"dasdsadsad\"}}";
        HashMap map = new Gson().fromJson(str, HashMap.class);
        Object o =  map.get("text");
        System.out.println(o);
    }

}
