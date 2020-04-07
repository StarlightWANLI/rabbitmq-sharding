package com.laowan.mq.shard.rabbitmqsharding.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.UUID;

/**
 * @program: rabbitmq-sharding
 * @description: rpc请求
 * @author: wanli
 * @create: 2020-04-07 16:25
 **/
@Component
@Slf4j
public class RPCClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "history";

    private static final String REPLY_TO = "history_reply";

    public String call(String message){
        String response = null;
        StopWatch stopWatch = new StopWatch("调用计时");
        stopWatch.start("rpc调用耗时");
        try {
            //设置消息唯一id
            String messageId = UUID.randomUUID().toString();
            CorrelationData correlationId = new CorrelationData(messageId);
            //直接发送message对象
            MessageProperties messageProperties = new MessageProperties();
            //过期时间10秒
            messageProperties.setExpiration("10000");
          //  rabbitTemplate.setReplyAddress(EXCHANGE_NAME);
         //   rabbitTemplate.expectedQueueNames();

           // message = message + messageId;
            Message message1 = new Message(message.getBytes(), messageProperties);
            Message message2 = rabbitTemplate.sendAndReceive(EXCHANGE_NAME, messageId, message1, correlationId);

            if (message2 != null) {
                 response = new String(message2.getBody());
                log.info("返回的消息为：{}",response);
            }
        }catch (Exception e){
            log.error("请求失败"+e.getMessage(),e);
        }
        stopWatch.stop();
        log.info(stopWatch.getLastTaskName()+stopWatch.getTotalTimeMillis()+"ms");
        return response;
    }

}
