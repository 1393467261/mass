package com.hzw.mass.controller;

import com.google.gson.Gson;
import com.hzw.mass.service.Text;
import com.hzw.mass.service.TextText;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.WxUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping("/send")
    public Map send(HttpServletRequest request){
        Map<Object, Object> list = new HashMap<>();

        Map<String, String[]> map = request.getParameterMap();
        String[] strings = map.get("type");
        if(strings == null){
            list.put("code", 1);
            list.put("info", "出错啦，无法判断数据类型");
            return list;
        }

        switch (strings[0]){
            case "text":
                Text text = new Text();
                text.setTouser("%s");
                text.setContent(new TextText(map.get("text[context]")[0]));
                int message_id = JdbcUtil.saveTextAndReturnId(text);

                try {
                    WxUtils.sendToQueue(text.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.put("code", 0);
                list.put("message_id", message_id);
                break;
            default:
                list.put("code", 1);
                list.put("info", "未知的消息类型");
                return list;
        }
        return list;
    }

}
