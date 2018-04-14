package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.entity.Article;
import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.Message;
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
                    if (picurl.length() > 5){
                        articleList.add(new Article(title, description, url, picurl));
                    }
                }

                String message = WxUtils.makePicAndTextMessage("%s", articleList);
                JdbcUtil.saveTextAndReturnId(message);
                WxUtils.sendToQueue(message);

                list.put("code", 0);
                list.put("info", "发送图文成功");
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
    *@Description: 长传图片到本地
    */
    @RequestMapping("/upload/picture")
    public String upload(@RequestParam("file")MultipartFile file, HttpServletRequest request){

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String filePath ="F:\\Project\\mass\\src\\main\\webapp\\img\\";
        String url = "img/" + fileName;

        try{
            UploadUtil.uploadFile(file.getBytes(), filePath, fileName);
        }catch(Exception e){
            e.printStackTrace();
        }

        return "{\"url\":\"" + url + "\"}";
    }
}
