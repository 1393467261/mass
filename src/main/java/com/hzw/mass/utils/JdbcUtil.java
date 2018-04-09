package com.hzw.mass.utils;

import com.hzw.mass.entity.Fail;
import com.hzw.mass.entity.Summary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Hzw
 * @Time: 2018/4/2 15:54
 * @Description:
 */
public class JdbcUtil {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://112.74.36.19:3306/wx";
    static final String USER = "admin";
    static final String PASS = "admin";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        return connection;
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection con){

        if (rs!=null && ps!=null && con!=null){

            try {
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(Connection connection, PreparedStatement ps) {

        if (connection!=null && ps!=null){

            try{
                ps.close();
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //传入到目前为止发送成功的数目和列的ID，更新数据库
    public static void success(Connection connection, Integer success, Integer id) throws SQLException {

        String sql = "update summary set success = ? where id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, success);
        ps.setInt(2, id);

        ps.execute();
        int updateCount = ps.getUpdateCount();
        System.out.println(updateCount);
        ps.close();
        if (updateCount < 1){
            throw new RuntimeException("更新成功的数目失败");
        }
    }
    //传入到目前为止发送失败的数目和列的ID，更新数据库
    public static void fail(Connection connection, Integer fail, Integer id) throws SQLException {

        String sql = "update summary set fail = ? where id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, fail);
        ps.setInt(2, id);

        ps.execute();
        int updateCount = ps.getUpdateCount();
        ps.close();
        if (updateCount < 1){
            throw new RuntimeException("更新fail的数目失败");
        }
    }
    //将上传图片获取的url和mediaId写入数据库
    public static void saveUrlAndMediaId(String url, String mediaId){

        try {
            String sql = "insert into summary(url, mediaId) values(?, ?)";
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, url);
            ps.setString(2, mediaId);
            ps.execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //返回当前summary表中最大的id
    public static Integer getMaxId(){

        int id = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;

        try {
            connection = getConnection();
            String sql = "select id from summary order by id desc limit 1";
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            set.next();
            id = set.getInt("id");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return id;
    }
    //将失败的集合写入数据库，返回写入的个数
    public static void saveFailList(Integer id, List<Fail> list){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;

        try{
            connection = getConnection();
            String sql = "insert into fail(summaryId, openId, errorCode) values(?, ?, ?)";
            ps = connection.prepareStatement(sql);
            for (Fail fail : list) {
                ps.setInt(1, id);
                ps.setInt(3, fail.getErrorCode());
                ps.setString(2, fail.getOpenId());
                ps.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(connection, ps);
        }
    }
    //查询数据库中的每条消息的记录，返回消息对象集合
    public static List<Summary> getSummaryList(){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "Select * from summary ORDER BY id DESC";
        List<Summary> list = new ArrayList<>();

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()){
                Summary summary = new Summary();
                summary.setId(set.getInt("id"));
                summary.setText(set.getString("text"));
                summary.setTitle(set.getString("title"));
                summary.setTime(set.getString("time"));
                list.add(summary);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return list;
    }
}
