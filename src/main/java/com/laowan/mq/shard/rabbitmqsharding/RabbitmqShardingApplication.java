package com.laowan.mq.shard.rabbitmqsharding;

import com.laowan.mq.shard.rabbitmqsharding.rpc.RPCClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableRabbit
@RestController
public class RabbitmqShardingApplication {

    @Autowired
    RPCClient rpcClient;

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqShardingApplication.class, args);
    }

    @GetMapping("/call")
    public String call(String message){
        return rpcClient.call(message);
    }
}
