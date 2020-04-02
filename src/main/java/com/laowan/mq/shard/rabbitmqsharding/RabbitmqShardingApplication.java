package com.laowan.mq.shard.rabbitmqsharding;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class RabbitmqShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqShardingApplication.class, args);
    }

}
