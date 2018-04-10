package com.hzw.mass;

import com.google.gson.Gson;
import com.hzw.mass.entity.*;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        System.out.println(JdbcUtil.getMaxId());
    }
}
