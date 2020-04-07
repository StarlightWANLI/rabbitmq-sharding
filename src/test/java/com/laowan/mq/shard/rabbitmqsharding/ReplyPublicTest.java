package com.laowan.mq.shard.rabbitmqsharding;

import com.rabbitmq.client.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * rpc调用测试
 */
public class ReplyPublicTest {
    private static final String EXCHANGE_NAME = "history";

    private static final String REPLY_TO = "history_reply";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("wanli");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .replyTo(REPLY_TO)
                .build();

       // channel.basicPublish("", "rpc_queue", props, message.getBytes());


      //  AMQP.BasicProperties.Builder bldr = new AMQP.BasicProperties.Builder();
       // for (int i = 0; i < 100000; i++) {
            //第一个参数是交换器名称，第二个参数是routeing key ，注意这里的routeing key一定要是随机的，不然消息都会发送到同一个队列中
            channel.basicPublish(EXCHANGE_NAME, UUID.randomUUID().toString(), props, "hello".getBytes("UTF-8"));
      //  }
      //  channel.waitForConfirmsOrDie(10000);

        TimeUnit.SECONDS.sleep(5);

        channel.close();
        connection.close();
    }
}
