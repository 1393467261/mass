package com.hzw.mass.wx;

import com.google.gson.Gson;
import com.hzw.mass.entity.Fail;
import com.hzw.mass.entity.Pojo;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.WxUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.List;

public class App {

    public static String ACCESS_TOKEN;
    public static Integer SUCCESS_COUNT = 1;
    public static Integer FAIL_COUNT = 6;
    public static Integer USER_COUNT = 30;

    public static void main(String[] args) throws Exception {

        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();

        String accessToken = WxUtils.getAccessToken();
        String content = "this is message1";
        //获取所有的openid
        UserList openIds = WxUtils.getOpenIds(accessToken);
        List<String> openIdList = openIds.getData().getOpenid();
        App.USER_COUNT = openIdList.size();
        //封装openid，加上count的属性,传入消息队列
        for (String s : openIdList) {
            String json = new Gson().toJson(new Pojo(s));
            WxUtils.sendToQueue(json, channel);
        }
        //处理队列中的数据
        List<Fail> fails = WxUtils.queueHandler(accessToken, content);
        //将队列中发送失败的写入数据库
        Integer id = JdbcUtil.getMaxId();
        JdbcUtil.saveFailList(id, fails);

        channel.close();
        connection.close();
    }
}
