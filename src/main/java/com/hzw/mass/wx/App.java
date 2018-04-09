package com.hzw.mass.wx;

import com.google.gson.Gson;
import com.hzw.mass.entity.Pojo;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.WxUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.List;

public class App {

    public static String ACCESS_TOKEN;

    public static void main(String[] args) throws Exception {

        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();

        String accessToken = "8_-37KijdHpBbAkD-iR9ku3XnVA9ba6zHpEFbK-s8cQlhDeAPv-bWj29tog87nSD-W_mtoCSW443qUj3gLILpxm9kDFraCKsWOaAoo5TB80i_YfrfZLsbgXzrn_t6lNjNcaDG9SIPalnvuRAuxYPUdABARIN";
        String content = "this is message1";
        //获取所有的openid
        UserList openIds = WxUtils.getOpenIds(accessToken);
        List<String> openIdList = openIds.getData().getOpenid();
        //封装openid，加上count的属性,传入消息队列
        for (String s : openIdList) {
            String json = new Gson().toJson(new Pojo(s));
            WxUtils.sendToQueue(json, channel);
        }
        //处理队列中的数据
        WxUtils.queueHandler(accessToken, content);

        channel.close();
        connection.close();
    }
}
