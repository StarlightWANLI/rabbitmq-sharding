package com.laowan.mq.shard.rabbitmqsharding;

import com.laowan.mq.shard.rabbitmqsharding.rpc.RPCClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqShardingApplicationTests {

    @Autowired
    RPCClient rpcClient;

    @Test
    void contextLoads() {
    }


    @Test
    void replyTest() throws Exception {
        for (int i = 0; i < 100000; i++) {
            System.out.println(rpcClient.call("小明"+i));
        }
    }



}
