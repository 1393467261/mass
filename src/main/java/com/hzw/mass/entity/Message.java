package com.hzw.mass.entity;

/**
 * @Author: Hzw
 * @Time: 2018/4/13 16:05
 * @Description: 对应数据库message表
 */
public class Message {

    private Integer message_id;
    private String text_plan;
    private String update_time;

    @Override
    public String toString() {
        return "Message{" +
                "message_id=" + message_id +
                ", text_plan='" + text_plan + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public Message(Integer message_id, String text_plan, String update_time) {
        this.message_id = message_id;
        this.text_plan = text_plan;
        this.update_time = update_time;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public String getText_plan() {
        return text_plan;
    }

    public void setText_plan(String text_plan) {
        this.text_plan = text_plan;
    }

    public String getUpdate_time() {
        return update_time.replace(".0", "");
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time.replace(".0", "");
    }
}
