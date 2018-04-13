package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.entity.UploadResp;
import com.hzw.mass.service.Text;
import com.hzw.mass.service.TextText;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @Author: Hzw
 * @Time: 2018/4/12 15:15
 * @Description:
 */
@RestController
public class Send {

    /**
    *@Description: 首先根据是否需要图片获取application中的数据，图文则获取url，图片则获取mediaId
    */
    @RequestMapping("/send")
    public Map send(@RequestParam(value = "file", required = false)MultipartFile file, HttpServletRequest request){
        Map<Object, Object> list = new HashMap<>();

        String type = request.getParameter("type");
        if(type == null){
            list.put("code", 1);
            list.put("info", "出错啦，无法判断数据类型");
            return list;
        }

        switch (type){
            case "text":
                Text text = new Text();
                text.setTouser("%s");
                text.setContent(new TextText(request.getParameter("text[context]")));
                int message_id = JdbcUtil.saveTextAndReturnId(text.toString());

                try {
                    WxUtils.sendToQueue(text.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.put("code", 0);
                list.put("info", "发送文字成功");
                break;

            case "news":
                //到此之前已经上传了图片并保存url到application中
                String title = request.getParameter("news[title]");
                String description = request.getParameter("news[description]");
                String url = (String) request.getSession().getServletContext().getAttribute("picurl");
                String message = WxUtils.makePicAndTextMessage("%s", title, description, url).replace(" ", "");
                JdbcUtil.saveTextAndReturnId(message);
                WxUtils.sendToQueue(message);
                list.put("code", 0);
                list.put("info", "发送图文消息成功");
                break;

            case "pic":
                String mediaId = (String) request.getSession().getServletContext().getAttribute("mediaid");
                String picMessage = WxUtils.makePictureMessage("%s", mediaId).replace(" ", "");
                JdbcUtil.saveTextAndReturnId(picMessage);
                WxUtils.sendToQueue(picMessage);
                list.put("code", 0);
                list.put("info", "发送图片成功");
                break;

            default:
                list.put("code", 1);
                list.put("info", "未知的消息类型");
                return list;
        }
        return list;
    }

    /**
    *@Description: 上传图片返回url，转发至send页面，将url,media_id存入application域对象中
    */
    @RequestMapping("/upload/picture")
    public ModelAndView test10(@RequestParam(value = "file", required = false)MultipartFile file){

        ModelAndView mv = new ModelAndView();
        String picurl = "";
        String mediaId = "";

        try {
            String resp = UploadUtil.postFile(WxUtils.getAccessToken(), file);
            UploadResp uploadResp = new Gson().fromJson(resp, UploadResp.class);
            picurl = uploadResp.getUrl();
            mediaId = uploadResp.getMedia_id();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalPicurl = picurl;
        String finalMediaId = mediaId;
        mv.setView(new View() {
            @Override
            public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
                httpServletRequest.getSession().getServletContext().setAttribute("picurl", finalPicurl);
                httpServletRequest.getSession().getServletContext().setAttribute("mediaid", finalMediaId);
                httpServletRequest.getRequestDispatcher("/send").forward(httpServletRequest, httpServletResponse);
            }
        });

        return mv;
    }

}
