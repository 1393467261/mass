package com.hzw.mass.wx;

import com.hzw.mass.utils.WxUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RbmqReceiver {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = WxUtils.getrabbitmqFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicConsume("queue_name", true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String json = new String(body);
                System.out.println(json);
            }
        });
    }
}
