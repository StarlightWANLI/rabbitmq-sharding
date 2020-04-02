package com.laowan.mq.shard.rabbitmqsharding;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ConsumerTest {
    private static final String QUEUE_NAME = "history";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("wanli");
        factory.setPort(5672);

         Connection connection = factory.newConnection();
         Channel channel = connection.createChannel();

        //每次抓取的消息数量
        channel.basicQos(32);

        for (int i = 0; i < 10; i++) {
            Consumer consumer = new MyConsumer(channel);
            channel.basicConsume(QUEUE_NAME,consumer);
        }

        TimeUnit.SECONDS.sleep(120);

        channel.close();
        connection.close();

    }


    private  static class MyConsumer extends DefaultConsumer{
        public MyConsumer(Channel channel) {
            super(channel);
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println("接收到的消息为：" + new String(body));
            super.getChannel().basicAck(envelope.getDeliveryTag(),false);
        }

    }

}
