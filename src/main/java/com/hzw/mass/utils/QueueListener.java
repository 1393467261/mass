package com.hzw.mass.utils;

import com.google.gson.Gson;
import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.Fail;
import com.hzw.mass.wx.App;
import com.rabbitmq.client.*;
import org.omg.CORBA.INTERNAL;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.hzw.mass.utils.WxUtils.getrabbitmqFactory;
import static com.hzw.mass.utils.WxUtils.sendToUser;

/**
 * @Author: Hzw
 * @Time: 2018/4/12 10:45
 * @Description: 监听队列中的消息，获取数据库中要发送的消息，发送处理，将成功失败的数据写入数据库
 */
public class QueueListener {

    public static void main(String[] args) {

        MyRunnable myRunnable = new MyRunnable("");
        Thread myThread1 = new Thread(myRunnable);
        myThread1.start();
    }
}
class MyRunnable implements Runnable{

    private String name;

    public MyRunnable(String name){
        this.name = name;
    }

    @Override
    public void run(){
        try {
            Connection connection = getrabbitmqFactory().newConnection();
            Channel channel = connection.createChannel();
            channel.basicConsume("queue_name", true, new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){

                    String json = new String(body);
                    System.out.println(json);
                    HashMap map = new Gson().fromJson(json, HashMap.class);
                    String msgtype = (String) map.get("msgtype");
                    System.out.println(msgtype);
                    App.ACCESS_TOKEN = WxUtils.getAccessToken();
                    List<String> openIdList = WxUtils.getOpenIds(App.ACCESS_TOKEN).getData().getOpenid();
//                    List<String> openIdList = new ArrayList<>();
//                    openIdList.add("oRUz80_FTbCClMbeHJLD6oHUeAqE");
//                    openIdList.add("oRUz80_FTbCClMbeHJLD6oHUeAqE");
                    Integer messageId = JdbcUtil.getIdByMessage(json);
                    switch (msgtype){
                        case "text":
                            //TODO 多线程发送消息
                            List<Fail> failList1 = new ArrayList<>();
                            for (String openId : openIdList) {
                                ErrorMsg textErrorMsg = sendToUser(App.ACCESS_TOKEN, String.format(json, openId));
                                System.out.println(textErrorMsg);
                                failList1.add(new Fail(openId, textErrorMsg.getErrcode()));
                            }
                            for (Fail fail : failList1) {
                                System.out.println(fail);
                            }

                            JdbcUtil.saveFailList(messageId, failList1);
                            break;

                        case "news":
                            List<Fail> failList2 = new ArrayList<>();
                            for (String openId : openIdList) {
                                ErrorMsg textErrorMsg = sendToUser(App.ACCESS_TOKEN, String.format(json, openId));
                                failList2.add(new Fail(openId, textErrorMsg.getErrcode()));
                            }
                            JdbcUtil.saveFailList(messageId, failList2);
                            break;

                        case "image":
                            List<Fail> failList3 = new ArrayList<>();
                            for (String openId : openIdList) {
                                ErrorMsg textErrorMsg = sendToUser(App.ACCESS_TOKEN, String.format(json, openId));
                                failList3.add(new Fail(openId, textErrorMsg.getErrcode()));
                            }
                            JdbcUtil.saveFailList(messageId, failList3);
                            break;
                    }

//                    //封装客户对象，包括openId和尝试次数，默认3次
//                    String json = new String(body);
//                    Pojo pojo = new Gson().fromJson(json, Pojo.class);
//                    //从数据库获取最新的要发送的消息
//                    Summary summary = JdbcUtil.getLatestMessage();
//                    //封装消息体
//                    String content = WxUtils.makePicAndTextMessage(pojo.getOpenId(),
//                            summary.getTitle(), summary.getText(), summary.getUrl());
//
//                    System.out.println("工作的线程的ID是" + Thread.currentThread().getId());
//
//                    //等于1，则重试一次，成功则成功数加一，失败就记录errorCode和对应的openId，进入集合作为返回值，同时将失败数加一
//                    if (pojo.getCounts() == 1){
//                        ErrorMsg errorMsg = sendToUser(App.ACCESS_TOKEN, content);
//
//                        //成功则成功数加一
//                        if (errorMsg.getErrcode() == 0){
//                            Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                            JdbcUtil.saveFail2Db(fail);
//                            App.SUCCESS_COUNT += 1;
//                        }
//
//                        //token失效，尝试次数不变，返回队列
//                        if (errorMsg.getErrcode() == 40001){
//                            App.ACCESS_TOKEN = WxUtils.getAccessToken();
//                            try {
//                                sendToQueue(new Gson().toJson(pojo));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        //不是token失效导致的
//                        if (errorMsg.getErrcode() != 40001 && errorMsg.getErrcode() != 0){
//                            Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                            JdbcUtil.saveFail2Db(fail);
//                            App.FAIL_COUNT += 1;
//                        }
//                    }
//
//                    //大于1，重试一次，若失败，次数减一，返回队列，若成功，成功数加一，消息被消费，发送成功
//                    if (pojo.getCounts() > 1){
//                        ErrorMsg errorMsg = sendToUser(App.ACCESS_TOKEN, content);
//                        Integer errorCode = errorMsg.getErrcode();
//
//                        //成功则成功数加一
//                        if (errorMsg.getErrcode() == 0){
//                            Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                            JdbcUtil.saveFail2Db(fail);
//                            App.SUCCESS_COUNT += 1;
//                        }
//
//                        //token失效，尝试次数不变，返回队列
//                        if (errorCode == 40001){
//                            App.ACCESS_TOKEN = WxUtils.getAccessToken();
//                            try {
//                                sendToQueue(new Gson().toJson(pojo));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        //不是token失效导致的
//                        if (errorCode != 40001 && errorCode != 0){
//                            try{
//                                pojo.decr();
//                                sendToQueue(new Gson().toJson(pojo));
//                            }catch (Exception e){
//                                throw new RuntimeException("error in queueHandler");
//                            }
//                        }
//                    }
                }
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

