package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.entity.*;
import com.hzw.mass.service.Text;
import com.hzw.mass.service.TextText;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.Utils;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 14:47
 * @Description: 推送消息给客户，显示历史消息及发送情况，统计实时发送情况
 */
@RestController
public class Controller {

    @RequestMapping("/send_old")
    public ModelAndView upload(@RequestParam(value = "my-file", required = false)MultipartFile file,
                         HttpServletRequest request) throws Exception {

        Connection connection = WxUtils.getrabbitmqFactory().newConnection();
        Channel channel = connection.createChannel();
        ModelAndView mv = new ModelAndView();
        String token = WxUtils.getAccessToken();
        String uploadJsonResp = UploadUtil.postFile(token, file);
        UploadResp uploadResp = new Gson().fromJson(uploadJsonResp, UploadResp.class);

        String title = "";
        String text = "";
        try{
            title = new String(request.getParameter("title").getBytes(), "utf-8");
            text = new String(request.getParameter("text").getBytes(), "utf-8");
        }catch(Exception e){
            e.printStackTrace();
        }

        JdbcUtil.saveUrlAndMediaId(uploadResp.getUrl(), uploadResp.getMedia_id(), title, text);
        String url = uploadResp.getUrl();
        //发送的openId集合
        List<String> openIdList = new ArrayList<>(Arrays.asList("oRUz80_FTbCClMbeHJLD6oHUeAqE",
                "oRUz80_FTbCClMbeHJLD6oHUeAqE",
                "oRUz80_FTbCClMbeHJLD6oHUeAqE"));
        App.USER_COUNT = openIdList.size();
        //封装openid，加上count的属性,传入消息队列
        for (String s : openIdList) {
            String json = new Gson().toJson(new Pojo(s));
            WxUtils.sendToQueue(json);
        }

        //转发至index
        mv.setView(new View() {
            @Override
            public void render(@Nullable Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
                httpServletResponse.sendRedirect("/index");
            }
        });

        channel.close();
        connection.close();

        return mv;
    }

    //消息发送程序的入口
    @RequestMapping("/index")
    public ModelAndView index(ModelAndView mv){

        List<Summary> summaryList = JdbcUtil.getSummaryList();
        mv.addObject("list", summaryList);
        mv.setViewName("/index");
        return mv;
    }

    //根据用户输入的消息进行回复
    @RequestMapping(value = "/wx")
    public void wx(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String message = null;
        response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Map<String, String> map = Utils.xmlToMap(request);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");

            System.out.println(fromUserName + toUserName + msgType + content);

            if ("text".equals(msgType)){
                TextMessage textMessage = new TextMessage();
                textMessage.setFromUserName(toUserName);
                textMessage.setToUserName(fromUserName);
                textMessage.setContent("你发送的信息是：" + content);
                textMessage.setMsgType("text");
                textMessage.setCreateTime(new Date().getTime());
                message = Utils.textMessasgeToXml(textMessage);
                System.out.println(message);
            }

            out.print(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //接收传入的id，返回id对应的消息的发送情况
    @RequestMapping("/chart")
    public ModelAndView chart(){

        ModelAndView mv = new ModelAndView();
        mv.setViewName("chart");

        return mv;
    }

    @RequestMapping("/template")
    public ModelAndView template(){

        ModelAndView mv = new ModelAndView();
        mv.setViewName("template");

        return mv;
    }

    @RequestMapping("/test")
    public ModelAndView test(){

        ModelAndView mv = new ModelAndView();
        mv.setViewName("test");

        return mv;
    }

}
