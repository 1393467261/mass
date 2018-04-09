package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.entity.Summary;
import com.hzw.mass.entity.TextMessage;
import com.hzw.mass.entity.UploadResp;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.Utils;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 14:47
 * @Description: 推送消息给客户，显示历史消息及发送情况，统计实时发送情况
 */
@RestController
public class Controller {

    //Ajax发送请求，返回已发送和总数百分比
    @RequestMapping(value = "/percent", method = RequestMethod.GET)
    public String getPercent(HttpServletResponse response){

        float f = (((float)App.SUCCESS_COUNT + (float)App.FAIL_COUNT)/(float)App.USER_COUNT) * 100;
        int i = (int)f;

        return "[{\"percent\":" + i + "}]";
    }

    //Ajax发送请求，返回发送失败的次数
    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public Integer getFailCount(){

        return App.FAIL_COUNT;
    }

    //Ajax发送请求，返回订阅者总数
    @RequestMapping(value = "/total", method = RequestMethod.GET)
    public Integer getUserCount(){

        return App.USER_COUNT;
    }

    //Ajax发送请求，返回成功发送的次数
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public Integer getSuccessCount(){

        return App.SUCCESS_COUNT;
    }

    @RequestMapping("/send")
    public String upload(@RequestParam(value = "my-file", required = false)MultipartFile file,
                         HttpServletRequest request) throws Exception {

        String token = WxUtils.getAccessToken();
        String uploadJsonResp = UploadUtil.postFile(token, file);
        UploadResp uploadResp = new Gson().fromJson(uploadJsonResp, UploadResp.class);
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        return uploadResp.getUrl() + "\n" + title + "\n" + text;
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

    @RequestMapping("/chart")
    public ModelAndView chart(ModelAndView mv){

        mv.setViewName("chart");

        return mv;
    }
}
