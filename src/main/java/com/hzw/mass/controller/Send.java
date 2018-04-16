package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.entity.*;
import com.hzw.mass.service.Text;
import com.hzw.mass.service.TextText;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
//    type: news
//    text[context]:
//    news[articles][0][title]: dd
//    news[articles][0][description]: dddd
//    news[articles][0][url]:
//    news[articles][0][picurl]:
//    news[articles][1][title]: ddddddddd
//    news[articles][1][description]: ddddddddddddd
//    news[articles][1][url]:
//    news[articles][1][picurl]:
//    json:
    @RequestMapping("/sendtest")
    public Map sendtest(HttpServletRequest request){

        Map<Object, Object> list = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String type = request.getParameter("type");
        if (type == null){
            list.put("code", 1);
            list.put("info", "出错了，无法判断数据类型");
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
                list.put("message_id", message_id);
                break;

            case "news":
                Set<String> keySet = parameterMap.keySet();
                Integer max = 0;
                for (String key : keySet) {
                    if (key.startsWith("news")){
                        char c = key.charAt(15);
                        int n = c - '0';
                        if (n > max){
                            max = n;
                        }
                    }
                }

                List<Article> articleList = new ArrayList<>();
                for (int i = 0; i < max + 1;i++){
                    String paraTitle = "news[articles][" + i + "][title]";
                    String paraDescription = "news[articles][" + i + "][description]";
                    String paraUrl = "news[articles][" + i + "][url]";
                    String paraPicUrl = "news[articles][" + i + "][picurl]";

                    String title = request.getParameter(paraTitle);
                    String description = request.getParameter(paraDescription);
                    String url = request.getParameter(paraUrl);
                    String picurl = request.getParameter(paraPicUrl);
                    if (title.length() < 1){
                        list.put("code", 1);
                        list.put("info", "输入标题");
                        return list;
                    }else if (description.length() < 1){
                        list.put("code", 1);
                        list.put("info", "输入内容");
                        return list;
                    }else if (url.length() < 1){
                        list.put("code", 1);
                        list.put("info", "输入外链url");
                        return list;
                    }else if (picurl.length() < 1){
                        list.put("code", 1);
                        list.put("info", "请上传图片");
                        return list;
                    }

                    articleList.add(new Article(title, description, url, picurl));
                }

                String message = WxUtils.makePicAndTextMessage("%s", articleList);
                Integer message_id_news = JdbcUtil.saveTextAndReturnId(message);
                WxUtils.sendToQueue(message);

                list.put("code", 0);
                list.put("info", "发送图文成功");
                list.put("message_id", message_id_news);
                break;

            case "image":
                String mediaId = request.getParameter("image[media_id]");
                String picMessage = WxUtils.makePictureMessage("%s", mediaId).replace(" ", "");
                Integer message_id_image = JdbcUtil.saveTextAndReturnId(picMessage);
                WxUtils.sendToQueue(picMessage);
                list.put("code", 0);
                list.put("info", "发送图片成功");
                list.put("message_id", message_id_image);
                break;

            default:
                list.put("code", 1);
                list.put("info", "未知的消息类型");
                return list;

        }

        return list;
    }

    /**
    *@Description: 首先根据是否需要图片获取application中的数据，图文则获取url，图片则获取mediaId
    */
    @RequestMapping("/send")
    public Map send(HttpServletRequest request){
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
//                type: news
//                news[articles][0][title]:
//                news[articles][0][description]:
//                news[articles][0][url]:
//                news[articles][0][picurl]:
//                news[articles][1][title]:
//                news[articles][1][description]:
//                news[articles][1][url]:
//                news[articles][1][picurl]:
//                news[articles][2][title]:
//                news[articles][2][description]:
//                news[articles][2][url]:
//                news[articles][2][picurl]:
//                news[articles][3][title]:
//                news[articles][3][description]:
//                news[articles][3][url]:
//                news[articles][3][picurl]:
//                "articles": [
//            {
//                "title":"Happy Day",
//                    "description":"Is Really A Happy Day",
//                    "url":"URL",
//                    "picurl":"PIC_URL"
//            },
//            {
//                "title":"Happy Day",
//                    "description":"Is Really A Happy Day",
//                    "url":"URL",
//                    "picurl":"PIC_URL"
//            }
//         ]
                List<Article> articleList = new ArrayList<>();

                for (int i = 0; i < 4;i++){
                    String title = null;
                    String description = null;
                    String url = null;
                    String picurl = null;

                    String paraTitle = "news[articles][" + i + "][title]";
                    String paraDescription = "news[articles][" + i + "][description]";
                    String paraUrl = "news[articles][" + i + "][url]";
                    String paraPicUrl = "news[articles][" + i + "][picurl]";

                    title = request.getParameter(paraTitle);
                    description = request.getParameter(paraDescription);
                    url = request.getParameter(paraUrl);
                    picurl = request.getParameter(paraPicUrl);

                    if (picurl.length() > 5){
                        articleList.add(new Article(title, description, url, picurl));
                    }
                }
                String message = WxUtils.makePicAndTextMessage("%s", articleList).replace("\\u003d", "=");
                JdbcUtil.saveTextAndReturnId(message);
                WxUtils.sendToQueue(message);
                list.put("code", 0);
                list.put("info", "发送图文消息成功");
                break;

            case "image":
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
    *@Description: 过时的方法，显然下面的更强大，上传图片返回url，转发至send页面，将url,media_id存入application域对象中
    */
    @RequestMapping("/upload/picture_old_old")
    public ModelAndView test10(@RequestParam(value = "file", required = false)MultipartFile file, HttpServletRequest request){

        ModelAndView mv = new ModelAndView();
        String picurl = "";
        String mediaId = "";
        System.out.println(request.getParameter("id"));

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

    @RequestMapping("/upload/picture_old")
    public String test(@RequestParam(value = "file", required = false)MultipartFile file){

        return new Gson().toJson(UploadUtil.postFile(WxUtils.getAccessToken(), file));
    }

    @RequestMapping("/history")
    public String getHistory() {

        return new Gson().toJson(JdbcUtil.getMessageList()).replace("\"{", "{").replace("}\"", "}").replace("\\u003d", "=").replace("\\", "");
    }

    /**
    *@Description: 上传图片到本地
    */
    @RequestMapping("/upload/picture")
    public String upload(@RequestParam("file")MultipartFile file, HttpServletRequest request) throws FileNotFoundException {

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        //String filePath ="F:\\Project\\mass\\src\\main\\webapp\\img\\";
        String filePath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/img/";
        String url = "img/" + fileName;
        try{
            UploadUtil.uploadFile(file.getBytes(), filePath, fileName);
        }catch(Exception e){
            e.printStackTrace();
        }

        return "{\"url\":\"" + url + "\"}";
    }
    /**
     *@Description: 由于图片发送需要media_id，所以上传到服务器
     */
    @RequestMapping("/upload/picture2")
    public Map upload2(@RequestParam("file")MultipartFile file, HttpServletRequest request){

        Map<Object, Object> list = new HashMap<>();
        String s = UploadUtil.postFile(WxUtils.getAccessToken(), file);
        UploadResp uploadResp = new Gson().fromJson(s, UploadResp.class);
        String id = uploadResp.getMedia_id();

        if (uploadResp == null){
            list.put("code", 1);
            list.put("info", "上传失败");
        }

        list.put("code", 0);
        list.put("media_id", id);

        return list;
    }
    /**
    *@Description: 对指定消息id的发送情况进行统计
    */
    //返回json
    @RequestMapping(value = "/collection")
    public Map collection(HttpServletRequest request){

        Map<Object, Object> list = new HashMap<>();

        String parameter = request.getParameter("message_id");

        Integer message_id = Integer.parseInt(parameter);
        List<ErrorTypeCollect> errorTypeCollect = JdbcUtil.getErrorTypeCollect(message_id);

        list.put("code", 0);
        list.put("info", "重发成功");
        list.put("json", errorTypeCollect);

        return list;
    }
    /**
    *@Description: 根据id跳转到对应的消息详情页面
    */
    @RequestMapping("/detail")
    public ModelAndView detail(@RequestParam("message_id")Integer message_id){

        ModelAndView mv = new ModelAndView();
        mv.setViewName("chart");

        List<ErrorTypeCollect> errorTypeCollect = JdbcUtil.getErrorTypeCollect(message_id);
        mv.addObject("collection", errorTypeCollect);
        mv.addObject("message_id", message_id);

        return mv;
    }
    /**
    *@Description: 传入messageId，错误状态码，重发给相应的客户，并修改数据库
    */
    @RequestMapping("/resend")
    public Map resend(@RequestParam("message_id")Integer message_id, @RequestParam("code")Integer code){

        App.ACCESS_TOKEN = WxUtils.getAccessToken();
        //查出对应的数据
        List<Fail> resendFailList = JdbcUtil.getFailListByCodeAndMessageId(message_id, code);
        //获取消息模板
        String textPlan = JdbcUtil.getTextPlanById(message_id);
        //遍历重发，更新数据库
        for (Fail fail : resendFailList) {
            ErrorMsg errorMsg = WxUtils.sendToUser(App.ACCESS_TOKEN, String.format(textPlan, fail.getOpenId()));
            JdbcUtil.updateFailCodeById(fail.getId(), errorMsg.getErrcode());
        }

        Map<Object, Object> list = new HashMap();
        list.put("code", 0);
        list.put("info", "重发成功");

        return list;
    }
    /**
    *@Description: 传入消息id，将其重发到未收到的客户中
    */
    @RequestMapping("/resend_fail")
    public Map resendFail(@RequestParam("message_id")Integer message_id){

        App.ACCESS_TOKEN = WxUtils.getAccessToken();
        List<Fail> failList = JdbcUtil.getFailExceptCodeIsZero(message_id);
        String textPlan = JdbcUtil.getTextPlanById(message_id);
        System.out.println(textPlan);

        for (Fail fail : failList) {
            ErrorMsg errorMsg = WxUtils.sendToUser(App.ACCESS_TOKEN, String.format(textPlan, fail.getOpenId()));
            System.out.println(errorMsg.getErrcode());
            JdbcUtil.updateFailCodeById(fail.getId(), errorMsg.getErrcode());
        }

        Map<Object, Object> list = new HashMap();
        list.put("code", 0);
        list.put("info", "重发成功");

        return list;
    }
    /**
     *@Description: 对指定消息进行重发,对象为所有人
     */
    @RequestMapping("/resend_all")
    public Map resend(@RequestParam("message_id")Integer message_id){

        Map<Object, Object> list = new HashMap<>();

        App.ACCESS_TOKEN = WxUtils.getAccessToken();
        List<Fail> failList = JdbcUtil.getFailByMessageId(message_id);
        String textPlan = JdbcUtil.getTextPlanById(message_id);

        for (Fail fail : failList) {
            ErrorMsg errorMsg = WxUtils.sendToUser(App.ACCESS_TOKEN, String.format(textPlan, fail.getOpenId()));
            JdbcUtil.updateFailCodeById(fail.getId(), errorMsg.getErrcode());
        }

        list.put("code", 0);
        list.put("info", "重发成功");

        return list;
    }
    /**
    *@Description: 获取用户总数
    */
    @RequestMapping("/total")
    public Integer getCustomer(){
        return JdbcUtil.getCustomerCount();
    }
    /**
    *@Description: 获取某条消息已经发送的个数
    */
    @RequestMapping("/sended")
    public Integer getSendedCount(@RequestParam("message_id")Integer id){
        return JdbcUtil.getSendedCount(id);
    }
}
