package com.gz.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbirMQTopicConfig {
    //定义了一个名为"seckillQueue"的队列
    private static final String QUEUE = "seckillQueue";
    //名为"seckillExchange"的交换机
    private static final String EXCHANGE = "seckillExchange";


    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }
//创建一个Topic模式的交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

//    创建了一个绑定将队列和交换机进行绑定，
//    使用了"seckill.#"作为路由键，
//    表示该绑定将所有以"seckill."开头的路由键的消息路由到"seckillQueue"队列
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
}
