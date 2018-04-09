package com.hzw.mass.wx;

import com.hzw.mass.utils.WxUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RbmqSender {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = WxUtils.getrabbitmqFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("queue_name", true, false, false, null);

        channel.basicPublish("", "queue_name", null, "hello world".getBytes());

        channel.close();
        connection.close();
    }
}
