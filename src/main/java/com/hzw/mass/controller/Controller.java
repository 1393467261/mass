package com.hzw.mass.controller;

import com.hzw.mass.utils.TextMessage;
import com.hzw.mass.utils.Utils;
import org.apache.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 14:47
 * @Description: 推送消息给客户，显示历史消息及发送情况，统计实时发送情况
 */
@RestController
public class Controller {

    @RequestMapping("/upload")
    public String upload(@RequestParam(value = "my-file", required = false)MultipartFile file,
                         HttpServletRequest request) throws Exception {

        String path = file.getOriginalFilename().toString();
        return path;
    }

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView mv){

        mv.setViewName("/index");
        return mv;
    }

    @RequestMapping("/send")
    public ModelAndView sendMsg(ModelAndView mv, @RequestParam("text")String text){
        /**
        *@Description: text为用户输入的数据
        */
        System.out.println(text);
        mv.setViewName("/index");
        return mv;
    }

    @RequestMapping(value = "/post")
    public String post(HttpServletRequest request){
        return request.getParameter("id") + 111222;
    }

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
//
//        return message;
    }
}
