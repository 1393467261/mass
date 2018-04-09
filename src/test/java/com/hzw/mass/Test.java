package com.hzw.mass;

import com.hzw.mass.entity.ErrorMsg;
import com.hzw.mass.entity.UserList;
import com.hzw.mass.utils.WxUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 16:26
 * @Description:
 */
public class Test {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        String accessToken = WxUtils.getAccessToken();
        UserList openIds = WxUtils.getOpenIds(accessToken);
        List<String> openidList = openIds.getData().getOpenid();

        for (String s : openidList) {
            System.out.println(s);
        }
        System.out.println(openidList.size());
    }
}
