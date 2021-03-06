package com.hzw.mass.utils;

import com.hzw.mass.entity.*;
import com.hzw.mass.service.Text;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.omg.CORBA.INTERNAL;

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

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        return connection;
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection con) {

        if (rs != null && ps != null && con != null) {

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

        if (connection != null && ps != null) {

            try {
                ps.close();
                connection.close();
            } catch (Exception e) {
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
        if (updateCount < 1) {
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
        if (updateCount < 1) {
            throw new RuntimeException("更新fail的数目失败");
        }
    }

    //将上传图片获取的url和mediaId写入数据库
    public static void saveUrlAndMediaId(String url, String mediaId, String title, String text) {

        try {
            String sql = "insert into summary(url, mediaId, title, text) values(?, ?, ?, ?)";
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, url);
            ps.setString(2, mediaId);
            ps.setString(3, title);
            ps.setString(4, text);
            ps.execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //返回当前summary表中最大的id
    public static Integer getMaxId() {

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
        } finally {
            close(set, ps, connection);
        }

        return id;
    }

    //将失败的集合写入数据库，返回写入的个数
    public static void saveFailList(Integer id, List<Fail> list) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            String sql = "INSERT into fail(summaryId, openId, errorCode) values(?, ?, ?)";
            ps = connection.prepareStatement(sql);
            for (Fail fail : list) {
                ps.setInt(1, id);
                ps.setInt(3, fail.getErrorCode());
                ps.setString(2, fail.getOpenId());
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, ps);
        }
    }

    //查询数据库中的每条消息的记录，返回消息对象集合
    public static List<Summary> getSummaryList() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "Select * from summary ORDER BY id DESC";
        List<Summary> list = new ArrayList<>();

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()) {
                Summary summary = new Summary();
                summary.setId(set.getInt("id"));
                summary.setText(set.getString("text"));
                summary.setTitle(set.getString("title"));
                summary.setTime(set.getString("time"));
                list.add(summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return list;
    }

    //返回统计错误种类及相应的数量
    public static List<ErrorTypeCollect> getErrorTypeCollect(Integer summaryId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "SELECT errorCode,COUNT(errorCode) as count FROM fail where summaryId = ? GROUP BY errorCode ";
        List<ErrorTypeCollect> list = new ArrayList<>();

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, summaryId);
            set = ps.executeQuery();
            while (set.next()) {
                list.add(new ErrorTypeCollect(set.getInt("errorCode"), set.getInt("count"), summaryId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return list;
    }

    //返回消息的图片和内容
    public static Summary getSummary(Integer id) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Summary summary = new Summary();
        String sql = "Select * from summary where id = ?";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            set = ps.executeQuery();
            set.next();
            summary.setTitle(set.getString("title"));
            summary.setText(set.getString("text"));
            summary.setMediaId(set.getString("mediaId"));
            summary.setUrl(set.getString("url"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return summary;
    }

    public static Summary getLatestMessage() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Summary summary = new Summary();
        String sql = "Select * from summary order by id desc limit 1";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            set.next();
            summary.setId(set.getInt("id"));
            summary.setTitle(set.getString("title"));
            summary.setText(set.getString("text"));
            summary.setMediaId(set.getString("mediaId"));
            summary.setUrl(set.getString("url"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return summary;
    }

    public static Boolean saveFail2Db(Fail fail) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Integer summaryId = JdbcUtil.getMaxId();
        Boolean execute = false;

        try {
            connection = getConnection();
            String sql = "insert into fail(summaryId, openId, errorCode) values(?, ?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, summaryId);
            ps.setString(2, fail.getOpenId());
            ps.setInt(3, fail.getErrorCode());
            execute = ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, ps);
        }

        return execute;
    }

    //传入所有客户的信息，写入数据库
    public static void saveCustomerInfo2Db(List<Customer> list) {

        Connection connection = null;
        PreparedStatement ps = null;
        String sql = "replace into customer(subscribe, openid, nickname, sex, city, country, province, language, headimgurl, subscribe_time," +
                "unionid, remark, groupid, subscribe_scene, qr_scene, qr_scene_str, update_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);

            for (Customer customer : list) {
                ps.setInt(1, customer.getSubscribe());
                ps.setString(2, customer.getOpenid());
                ps.setString(3, customer.getNickname());
                ps.setInt(4, customer.getSex());
                ps.setString(5, customer.getCity());
                ps.setString(6, customer.getCountry());
                ps.setString(7, customer.getProvince());
                ps.setString(8, customer.getLanguage());
                ps.setString(9, customer.getHeadimgurl());
                ps.setInt(10, customer.getSubscribe_time());
                ps.setString(11, customer.getUnionid());
                ps.setString(12, customer.getRemake());
                ps.setInt(13, customer.getGroupid());
                ps.setString(14, customer.getSubscribe_scene());
                ps.setInt(15, customer.getQr_scene());
                ps.setString(16, customer.getQr_scene_str());
                ps.setLong(17, System.currentTimeMillis());
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, ps);
        }
    }

    //保存消息，返回id
    public static Integer saveTextAndReturnId(String text) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Integer id = null;
        String sql = "insert into message(text_plan) values(?)";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, text);
            ps.execute();
            set = ps.getGeneratedKeys();
            if (set.next()) {
                id = set.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    //根据消息体查询对应的id
    public static Integer getIdByMessage(String message) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Integer id = null;
        String sql = "select message_id from message where text_plan = ? order by message_id desc limit 1";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, message);
            set = ps.executeQuery();
            set.next();
            id = set.getInt("message_id");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return id;
    }

    //获取message表中的数据并封装
    public static List<Message> getMessageList() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        List<Message> messageList = new ArrayList<>();
        String sql = "select * from message ORDER BY update_time desc";

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()) {
                messageList.add(new Message(set.getInt("message_id"),
                        set.getString("text_plan"),
                        set.getString("update_time").replace(".0", "")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(set, ps, connection);
        }

        return messageList;
    }

    //根据消息id获取text_plan,进行重发
    public static String getTextPlanById(Integer id){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "select text_plan from message where message_id = ?";
        String textPlan = "";

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            set = ps.executeQuery();
            set.next();
            textPlan = set.getString("text_plan");
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return textPlan;
    }

    /**
    *@Description: 根据状态码和messageId查找对应的发送失败的记录
    */
    public static List<Fail> getFailListByCodeAndMessageId(Integer messageId, Integer code){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "select * from fail where errorCode = ? and summaryId = ?";
        List<Fail> list = new ArrayList<>();

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, code);
            ps.setInt(2, messageId);
            set = ps.executeQuery();
            while (set.next()){
                list.add(new Fail(set.getInt("id"), set.getString("openId")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return list;
    }
    /**
    *@Description: 传入要更新的记录的id和状态码,更新数据库
    */
    public static void updateFailCodeById(Integer id, Integer code){

        Connection connection = null;
        PreparedStatement ps = null;
        String sql = "update fail set errorCode = ? where id = ?";

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, code);
            ps.setInt(2, id);
            ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(connection, ps);
        }
    }
    /**
    *@Description: 传入messageId，查询该消息未发送成功即errorCode不等于0的用户
    */
    public static List<Fail> getFailExceptCodeIsZero(Integer messageId){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "select * from fail where errorCode != 0 and summaryId = ?";
        List<Fail> list = new ArrayList<>();

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            set = ps.executeQuery();
            while (set.next()){
                list.add(new Fail(set.getInt("id"), set.getString("openId")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return list;
    }
    /**
    *@Description: 根据messageId获取所有客户
    */
    public static List<Fail> getFailByMessageId(Integer messageId){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        String sql = "select * from fail where summaryId = ?";
        List<Fail> list = new ArrayList<>();

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            set = ps.executeQuery();
            while (set.next()){
                list.add(new Fail(set.getInt("id"), set.getString("openId")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return list;
    }
    /**
    *@Description: 获取数据库中用户总数
    */
    public static Integer getCustomerCount(){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Integer count = 0;
        String sql = "select count(*) as count from customer";

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            set = ps.executeQuery();
            set.next();
            count = set.getInt("count");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return count;
    }
    /**
    *@Description: 获取某条消息已经发送给用户的个数
    */
    public static Integer getSendedCount(Integer message_id){

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Integer count = 0;
        String sql = "select count(*) as count from fail where summaryId = ?";

        try{
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, message_id);
            set = ps.executeQuery();
            set.next();
            count = set.getInt("count");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(set, ps, connection);
        }

        return count;
    }
}
