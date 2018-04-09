package com.hzw.mass;

import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 16:26
 * @Description:
 */
public class Test {

    public static void main(String[] args){

        System.out.println(((float)App.SUCCESS_COUNT+(float)App.FAIL_COUNT)/(float)App.USER_COUNT);
    }
}
