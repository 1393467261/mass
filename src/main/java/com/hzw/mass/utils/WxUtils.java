package com.hzw.mass.utils;

import com.google.gson.Gson;
import com.hzw.mass.entity.*;
import com.hzw.mass.service.Text;
import com.hzw.mass.service.TextText;
import com.hzw.mass.wx.*;
import com.rabbitmq.client.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class WxUtils {

    //获取rabbitmq连接
    public static ConnectionFactory getrabbitmqFactory(){

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.31.2");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        factory.setVirtualHost("/guest");

        return factory;
    }

    //获取文本信息模板
    public static String makeTextCustomMessage(String openId, String content) {
        content = content.replace("\"", "\\\"");
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
        return String.format(jsonMsg, openId, content);
    }
    //获取图片信息模板
    public static String makePictureMessage(String openId, String mediaId) {
        String jsonMsg = "{" +
                "\"touser\":\"%s\"," +
                "\"msgtype\":\"image\"," +
                "\"image\":" +
                "{" +
                "\"media_id\":\"%s\"" +
                "}" +
                "}";
        return String.format(jsonMsg, openId, mediaId);
    }
    //获取图文消息
    public static String makePicAndTextMessage(String openId, List<Article> list){
//        title = title.replace("\"", "\\\"");
//        description = description.replace("\"", "\\\"");
//        String jsonMsg = "{" +
//                "\"touser\":\"%s\"," +
//                "\"msgtype\":\"news\"," +
//                "\"news\":{" +
//                "\"articles\": [" +
//                "{" +
//                "\"title\":\"%s\"," +
//                "\"description\":\"%s\"," +
//                "\"url\":\"url\"," +
//                "\"picurl\":\"%s\"" +
//                "}" +
//                "]" +
//                "}" +
//                "}";
        String articles = new Gson().toJson(list);
        String jsonMsg = "{" +
                "\"touser\":\"%s\"," +
                "\"msgtype\":\"news\"," +
                "\"news\":{" +
                "\"articles\":" + articles +
                "}" +
                "}";


        return String.format(jsonMsg, openId);
    }
    //通过accessToken获取用户信息
    public static UserList getOpenIds(String accessToken){
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" +
                accessToken;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String result="";

        try {
            HttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(),"utf-8");
        } catch (Exception e) {
        }
        return new Gson().fromJson(result, UserList.class);
    }
    //获取accessToken
    public static String getAccessToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx069ae70becb48311&secret=120b94fe688cfd83cca0af47812b466f";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String result = "";

        try{
            HttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            throw new RuntimeException("获取AccessToken失败");
        }

        return new Gson().fromJson(result, AccessToken.class).getAccess_token();
    }

    //发送消息给用户
    public static ErrorMsg sendToUser(String accessToken, String message){
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
                accessToken;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String result = "";
        ErrorMsg errorMsg = null;

        try {
            httpPost.setEntity(new StringEntity(message, "utf-8"));
            HttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(),"utf-8");
            errorMsg = new Gson().fromJson(result, ErrorMsg.class);
            System.out.println(errorMsg);
        } catch (Exception e) {
            throw new RuntimeException("发送消息失败");
        }

        return errorMsg;
    }

    //发送消息到队列中
    public static void sendToQueue(String string){
        Channel channel = null;
        try {
            channel = getrabbitmqFactory().newConnection().createChannel();
            channel.basicPublish("", "queue_name", null, string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //根据调用微信服务器接口返回值判断，若失败，判断count是否小于1，若小于1，则丢弃该消息，否则count-1，返回给队列，返回的是发送失败的fail集合
    public static List<Fail> queueHandler(String title, String text, String url) throws IOException, TimeoutException {
        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();
        List<Fail> list = new ArrayList<>();

        channel.basicConsume("queue_name", true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String json = new String(body);
                Object obj = new Gson().fromJson(json, Object.class);
                try {
                    Method m = obj.getClass().getMethod("getMsgtype", null);
                    Object o = m.invoke(obj);
                    String msgtype = o.toString();
                    System.out.println(msgtype);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

//                //封装消息
//                String content = WxUtils.makePicAndTextMessage(pojo.getOpenId(), title, text, url);
//
//                //等于1，则重试一次，成功则成功数加一，失败就记录errorCode和对应的openId，进入集合作为返回值，同时将失败数加一
//                if (pojo.getCounts() == 1){
//                    ErrorMsg errorMsg = sendToUser(App.ACCESS_TOKEN, content);
//
//                    //成功则成功数加一
//                    if (errorMsg.getErrcode() == 0){
//                        Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                        list.add(fail);
//                        App.SUCCESS_COUNT += 1;
//                    }
//
//                    //token失效，尝试次数不变，返回队列
//                    if (errorMsg.getErrcode() == 40001){
//                        App.ACCESS_TOKEN = WxUtils.getAccessToken();
//                        try {
//                            sendToQueue(new Gson().toJson(pojo));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    //不是token失效导致的
//                    if (errorMsg.getErrcode() != 40001 && errorMsg.getErrcode() != 0){
//                        Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                        list.add(fail);
//                        App.FAIL_COUNT += 1;
//                    }
//                }
//
//                //大于1，重试一次，若失败，次数减一，返回队列，若成功，成功数加一，消息被消费，发送成功
//                if (pojo.getCounts() > 1){
//                    ErrorMsg errorMsg = sendToUser(App.ACCESS_TOKEN, content);
//                    Integer errorCode = errorMsg.getErrcode();
//
//                    //成功则成功数加一
//                    if (errorMsg.getErrcode() == 0){
//                        Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
//                        list.add(fail);
//                        App.SUCCESS_COUNT += 1;
//                    }
//
//                    //token失效，尝试次数不变，返回队列
//                    if (errorCode == 40001){
//                        App.ACCESS_TOKEN = WxUtils.getAccessToken();
//                        try {
//                            sendToQueue(new Gson().toJson(pojo));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    //不是token失效导致的
//                    if (errorCode != 40001 && errorCode != 0){
//                        try{
//                        pojo.decr();
//                        sendToQueue(new Gson().toJson(pojo));
//                        }catch (Exception e){
//                            throw new RuntimeException("error in queueHandler");
//                        }
//                    }
//                }
            }
        });

        return list;
    }
    //所有消息发送完成后的处理
    public static void afterSend(Integer summaryId, List<Fail> list){

        java.sql.Connection connection = null;
        //将发送成功和失败的写入数据库
        try {
            connection = JdbcUtil.getConnection();
            JdbcUtil.success(connection, App.SUCCESS_COUNT, summaryId);
            JdbcUtil.fail(connection, App.FAIL_COUNT, summaryId);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //所有数据置为0
        App.USER_COUNT = 0;
        App.FAIL_COUNT = 0;
        App.SUCCESS_COUNT = 0;
        //将失败的写入数据库
        JdbcUtil.saveFailList(summaryId, list);
    }

    //通过openId获取客户信息
    public static Customer getCustomerInfo(String openId){

        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + App.ACCESS_TOKEN + "&openid=" + openId + "&lang=zh_CN";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String result="";

        try {
            HttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(),"utf-8");
        } catch (Exception e) {
        }
        return new Gson().fromJson(result, Customer.class);
    }

    //传入openId集合，获取客户对象集合
    public static List<Customer> getCustomerInfoList(List<String> openIdList){

        List<Customer> customerInfoList = new ArrayList<>();

        for (String s : openIdList) {
            Customer customerInfo = getCustomerInfo(s);

            if (customerInfo == null){
                App.ACCESS_TOKEN = WxUtils.getAccessToken();
            }

            customerInfoList.add(customerInfo);
        }

        return customerInfoList;
    }

    //传入text_plan,获取相应的消息类型对象
    public static Object getMessageByTextPlan(String textPlan){

        //获取共有字段msgtype判断类型
        HashMap hashMap = new Gson().fromJson(textPlan, HashMap.class);
        String msgtype = (String) hashMap.get("msgtype");

        //封装文本消息
        if (msgtype.equals("text")){
            return new Gson().fromJson(textPlan, Text.class);
        }

        return null;
    }
}
