package com.laowan.mq.shard.rabbitmqsharding.config;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.io.IOException;

@Configuration
@Slf4j
public class HistoryConsumerConfig {
    private static final String QUEUE_NAME = "history";

    private static final String REPLY_TO = "history_reply";

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) throws IOException {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
      //  container.setQueueNames(QUEUE_NAME);
        container.setExposeListenerChannel(true);
        // 设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        Channel channel = connectionFactory.createConnection().createChannel(false);
        //反馈
        for (int i = 0; i < 20; i++) {
            //每次抓取的消息数量
            channel.basicQos(32);
            Consumer consumer = new MyConsumer(channel);
            channel.basicConsume(QUEUE_NAME,false,consumer);

           // channel.basicConsume()
        }
        return container;
    }

    public static String sayHello(String name){
                return "hello " + name ;
        }

    private  static class MyConsumer extends DefaultConsumer {
        public MyConsumer(Channel channel) {
            super(channel);
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            log.info("接收到的消息为：" + new String(body));
            StopWatch stopWatch = new StopWatch("调用计时");
            stopWatch.start("rpc调用消费者耗时");
            Channel channel = super.getChannel();
            AMQP.BasicProperties responseProps = new AMQP.BasicProperties.Builder()
                        .correlationId(properties.getCorrelationId())
                        .replyTo(QUEUE_NAME)
                        .build() ;

           //将结果返回到客户端Queue
            String responseMessage = sayHello(new String(body)) ;

            //模拟消息耗时堵塞
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //交换器，routeking key
            channel.basicPublish("", properties.getReplyTo() , responseProps , responseMessage.getBytes("UTF-8") ) ;

            //向客户端确认消息
            super.getChannel().basicAck(envelope.getDeliveryTag(),false);
            stopWatch.stop();
            log.info(stopWatch.getLastTaskName()+stopWatch.getTotalTimeMillis()+"ms");
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
