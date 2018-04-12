package com.hzw.mass.wx;

import com.google.gson.Gson;
import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.Fail;
import com.hzw.mass.entity.Pojo;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.WxUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    public static String ACCESS_TOKEN;
    public static Integer SUCCESS_COUNT = 0;
    public static Integer FAIL_COUNT = 0;
    public static Integer USER_COUNT = 0;
    public static Integer MAX_ID = JdbcUtil.getMaxId();

    public static void main(String[] args) throws Exception {

        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();

        String accessToken = WxUtils.getAccessToken();
        String content = "this is text message";
        //获取所有的openid
        //TODO 上传图片，返回url，id写入数据库，修改App.MAX_ID
//        UserList openIds = WxUtils.getOpenIds(accessToken);
//        List<String> openIdList = openIds.getData().getOpenid();
        List<String> openIdList = new ArrayList<>(Arrays.asList("oRUz80_FTbCClMbeHJLD6oHUeAqE",
                "oRUz80_FTbCClMbeHJLD6oHUeAqE",
                "oRUz80_FTbCClMbeHJLD6oHUeAqE"));
        App.USER_COUNT = openIdList.size();
        //封装openid，加上count的属性,传入消息队列
        for (String s : openIdList) {
            String json = new Gson().toJson(new Pojo(s));
            WxUtils.sendToQueue(json);
        }
        //处理队列中的数据
        List<Fail> fails = WxUtils.queueHandler("1", "2", content);
        //将队列中发送失败的写入数据库
        Integer id = JdbcUtil.getMaxId();
        //后续处理
        WxUtils.afterSend(id, fails);

        channel.close();
        connection.close();
    }
}
