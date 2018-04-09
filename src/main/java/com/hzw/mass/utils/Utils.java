package com.hzw.mass.utils;

import com.hzw.mass.entity.TextMessage;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Hzw
 * @Time: 2018/4/8 14:19
 * @Description:
 */
public class Utils {

    public static Map<String,String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
        /**
        *@Description: 解析请求中的xml参数，放入map中
        */
        Map<String,String> map = new HashMap<String,String>();
        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);

        //获取根节点
        Element root = doc.getRootElement();

        List<Element> list = root.elements();

        for(Element e : list){
            map.put(e.getName(), e.getText());
        }
        ins.close();

        return map;
    }

    public static String textMessasgeToXml(TextMessage textMessage){
        /**
        *@Description: 将TextMessage对象中的数据封装进xml
        */
        XStream xStream = new XStream();
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }
}
