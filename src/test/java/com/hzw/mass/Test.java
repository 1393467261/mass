package com.hzw.mass;

import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 16:26
 * @Description:
 */
public class Test {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        String token = "8_Mqjso8hD_SBiyEYUZh_KId8N9LpCQjmLT3-z5ICYGYKsxqcirrAEiVNw9WNUUBhW21AKc-awteFU6OhB8AjllFAsSLyv6RmiyhYvpUhi8jPOcgywErInaFQtCevDaFg-4KIgIkKolGh6tZvnITHdADACNB";
        String s = UploadUtil.postFile(token, "D:\\Documents\\啄木鸟公司\\logo.jpg");
        System.out.println(s);
        System.out.println(s);
    }
}
