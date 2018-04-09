package com.hzw.mass.utils;

import com.google.gson.Gson;
import com.hzw.mass.entity.*;
import com.hzw.mass.wx.*;
import com.rabbitmq.client.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
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
        String jsonMsg = "{\n" +
                "    \"touser\":\"%s\",\n" +
                "    \"msgtype\":\"image\",\n" +
                "    \"image\":\n" +
                "    {\n" +
                "      \"media_id\":\"%s\"\n" +
                "    }\n" +
                "}";
        return String.format(jsonMsg, openId, mediaId);
    }
    //获取图文消息
    public static String makePicAndTextMessage(String openId, String title, String description, String picUrl){
        title = title.replace("\"", "\\\"");
        description = description.replace("\"", "\\\"");
        picUrl = picUrl.replace("\\", "");
        String jsonMsg = "{\n" +
                "    \"touser\":\"%s\",\n" +
                "    \"msgtype\":\"news\",\n" +
                "    \"news\":{\n" +
                "        \"articles\": [\n" +
                "         {\n" +
                "             \"title\":\"%s\",\n" +
                "             \"description\":\"%s\",\n" +
                "             \"url\":\"url\",\n" +
                "             \"picurl\":\"%s\"\n" +
                "         }\n" +
                "         ]\n" +
                "    }\n" +
                "}";
        return String.format(jsonMsg, openId, title, description, picUrl);
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
            System.out.println(result);
            errorMsg = new Gson().fromJson(result, ErrorMsg.class);
            System.out.println(errorMsg);
        } catch (Exception e) {
            throw new RuntimeException("发送消息失败");
        }

        return errorMsg;
    }

    //发送消息到队列中
    public static void sendToQueue(String json, Channel channel) throws Exception{

        channel.basicPublish("", "queue_name", null, json.getBytes());
    }

    //根据调用微信服务器接口返回值判断，若失败，判断count是否小于1，若小于1，则丢弃该消息，否则count-1，返回给队列，返回的是发送失败的fail集合
    public static List<Fail> queueHandler(String accessToken, String content) throws IOException, TimeoutException {
        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();
        List<Fail> list = new ArrayList<>();

        channel.basicConsume("queue_name", true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String json = new String(body);
                Pojo pojo = new Gson().fromJson(json, Pojo.class);

                //等于1，则重试一次，成功则不处理，失败就记录errorCode和对应的openId，进入集合作为返回值
                if (pojo.getCounts() == 1){
                    ErrorMsg errorMsg = sendToUser(accessToken, makeTextCustomMessage(pojo.getOpenId(), content));

                    //token失效，尝试次数不变，返回队列
                    if (errorMsg.getErrcode() == 40001){
                        App.ACCESS_TOKEN = WxUtils.getAccessToken();
                        try {
                            sendToQueue(new Gson().toJson(pojo), channel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //不是token失效导致的
                    if (errorMsg.getErrcode() != 40001 && errorMsg.getErrcode() != 0){
                        Fail fail = new Fail(pojo.getOpenId(), errorMsg.getErrcode());
                        list.add(fail);
                    }
                }

                //大于1，重试一次，若失败，次数减一，返回队列，若成功，不处理，消息被消费，发送成功
                if (pojo.getCounts() > 1){
                    ErrorMsg errorMsg = sendToUser(accessToken, makeTextCustomMessage(pojo.getOpenId(), content));
                    Integer errorCode = errorMsg.getErrcode();

                    //token失效，尝试次数不变，返回队列
                    if (errorCode == 40001){
                        App.ACCESS_TOKEN = WxUtils.getAccessToken();
                        try {
                            sendToQueue(new Gson().toJson(pojo), channel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //不是token失效导致的
                    if (errorCode != 40001 && errorCode != 0){
                        try{
                        pojo.decr();
                        sendToQueue(new Gson().toJson(pojo), channel);
                        }catch (Exception e){
                            throw new RuntimeException("error in queueHandler");
                        }
                    }
                }
            }
        });

        return list;
    }
}
