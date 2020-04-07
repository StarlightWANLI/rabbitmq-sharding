package com.laowan.mq.shard.rabbitmqsharding.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @program: rabbitmq-sharding
 * @description:
 * @author: wanli
 * @create: 2020-04-07 16:26
 **/
public class QueueingConsumer extends DefaultConsumer {
    public QueueingConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("接收到的消息为：" + new String(body));
        //反馈
        super.getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
