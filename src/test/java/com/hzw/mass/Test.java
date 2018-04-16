//package com.hzw.mass;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.hzw.mass.controller.Send;
//import com.hzw.mass.entity.*;
//import com.hzw.mass.service.Text;
//import com.hzw.mass.service.TextText;
//import com.hzw.mass.utils.JdbcUtil;
//import com.hzw.mass.utils.UploadUtil;
//import com.hzw.mass.utils.WxUtils;
//import com.hzw.mass.wx.App;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.swing.plaf.metal.OceanTheme;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.*;
//
///**
// * @Author: Hzw
// * @Time: 2018/4/2 16:26
// * @Description: 我的openId oRUz80_FTbCClMbeHJLD6oHUeAqE
// */
//public class Test {
//
//    public static void main(String[] args){
//
//        String token = WxUtils.getAccessToken();
//        String content = "hello";
//        String message = WxUtils.makeTextCustomMessage("oRUz80_FTbCClMbeHJLD6oHUeAqE", content);
//
//        ErrorMsg errorMsg = WxUtils.sendToUser(token, message);
//        List<Fail> list = new ArrayList<>();
//        list.add(new Fail("oRUz80_FTbCClMbeHJLD6oHUeAqE", errorMsg.getErrcode()));
//
//        WxUtils.afterSend(JdbcUtil.getMaxId(), list);
//    }
//
//    @org.junit.Test
//    public void test2(){
//
//        App.ACCESS_TOKEN = WxUtils.getAccessToken();
//        UserList openIds = WxUtils.getOpenIds(App.ACCESS_TOKEN);
//        List<String> openidList = openIds.getData().getOpenid();
//        List<Customer> customerInfoList = WxUtils.getCustomerInfoList(openidList);
//        JdbcUtil.saveCustomerInfo2Db(customerInfoList);
//    }
//
//    @org.junit.Test
//    public void test3(){
//        String str = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"dasdsadsad\"}}";
//        HashMap map = new Gson().fromJson(str, HashMap.class);
//        Object o =  map.get("text");
//        System.out.println(o);
//    }
//
//    @org.junit.Test
//    public void test4(){
//
//        Connection connection = null;
//        PreparedStatement ps = null;
//        ResultSet set = null;
//        String sql = "select * from customer";
//
//        try{
//            connection = JdbcUtil.getConnection();
//            ps = connection.prepareStatement(sql);
//            set = ps.executeQuery();
//            while (set.next()){
//                InputStream in = set.getBinaryStream("nickname");
//                String nickName = "";
//                int len = 0;
//                byte buffer[] = new byte[1024];
//                while ((len = in.read(buffer)) > 0) {
//                    nickName += new String(buffer);
//                    }
//                in.close();
//                System.out.println(nickName);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            JdbcUtil.close(set, ps, connection);
//        }
//    }
//
//    @org.junit.Test
//    public void test5(){
//
//
//        Connection connection = null;
//        PreparedStatement ps = null;
//        ResultSet set = null;
//        String sql = "select nickNAME from customer";
//
//        try{
//            connection = JdbcUtil.getConnection();
//            ps = connection.prepareStatement(sql);
//            set = ps.executeQuery();
//            while (set.next()){
//                String nickName = set.getString("nickname");
//                System.out.println(nickName);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            JdbcUtil.close(set, ps, connection);
//        }
//    }
//
//    @org.junit.Test
//    public void test6(){
//
//        Integer id = JdbcUtil.getIdByMessage("{\n" +
//                "\"touser\":\"%s\",\n" +
//                "\"msgtype\":\"image\",\n" +
//                "\"image\":\n" +
//                "{\n" +
//                "\"media_id\":\"vM4jLiJNU3Wu0prV5eEeaGHBqXLbBEeORGjVyfntJVY\"\n" +
//                "}\n" +
//                "}");
//        System.out.println(id);
//    }
//    @org.junit.Test
//    public void test7(){
//
//        for (Message message : JdbcUtil.getMessageList()) {
//            System.out.println(message);
//        }
//    }
//
//    @org.junit.Test
//    public void test8(){
//
//    }
//
//    @org.junit.Test
//    public void test9(){
//
//        TextText textText = new TextText("hello hzw");
//        Text text = new Text();
//        text.setContent(textText);
//        text.setTouser("jame");
//        text.setMsgtype("text");
//        text.setUpdate_time("2018");
//
//        System.out.println(new Gson().toJson(text));
//    }
//
//    @org.junit.Test
//    public void test10(){
//
//        System.out.println(new Gson().toJson(JdbcUtil.getMessageList()).replace("\"{", "{").replace("}\"", "}").replace("\\u003d", "=").replace("\\", ""));
//    }
//
//    @org.junit.Test
//    public void test11(){
//
//        System.out.println(Send.class.getClass().getResource("/").getPath());
//    }
//}
