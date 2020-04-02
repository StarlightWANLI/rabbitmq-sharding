package com.laowan.mq.shard.rabbitmqsharding.config;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HistoryConsumerConfig {
    private static final String QUEUE_NAME = "history";

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) throws IOException {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
      //  container.setQueueNames(QUEUE_NAME);
        container.setExposeListenerChannel(true);
        // 设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        Channel channel = connectionFactory.createConnection().createChannel(false);
        //反馈
        for (int i = 0; i < 50; i++) {
            //每次抓取的消息数量
            channel.basicQos(32);
            Consumer consumer = new MyConsumer(channel);
            channel.basicConsume(QUEUE_NAME,false,consumer);
        }
        return container;
    }

    private  static class MyConsumer extends DefaultConsumer {
        public MyConsumer(Channel channel) {
            super(channel);
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println("接收到的消息为：" + new String(body));
            //反馈
            super.getChannel().basicAck(envelope.getDeliveryTag(),false);
        }

    }

  /*  @Bean
    public SimpleMessageListenerContainer messageContainer2(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(new Queue("hello"));
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        // 设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                byte[] body = message.getBody();
                System.out.println("  2  receive msg : " + JSONObject.parseObject(new String(body)));
                //不读取消息并且将当前消息抛弃掉，消息队列中删除当前消息
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                //不读取消息，消息队列中保留当前消息未被查看状态
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

                //确认消息成功消费，删除消息队列中的消息
                // channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                //确认消息成功消费，删除消息队列中的消息，他跟上面貌似一样
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            }
        });
        return container;
    }*/
}
