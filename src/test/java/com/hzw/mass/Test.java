package com.hzw.mass;

import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.Summary;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.JdbcUtil;
import com.hzw.mass.utils.UploadUtil;
import com.hzw.mass.utils.WxUtils;
import com.hzw.mass.wx.App;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 16:26
 * @Description: 我的openId oRUz80_FTbCClMbeHJLD6oHUeAqE
 */
public class Test {

    public static void main(String[] args){

        List<Summary> summaryList = JdbcUtil.getSummaryList();
        for (Summary summary : summaryList) {
            System.out.println(summary);
        }
    }
}
