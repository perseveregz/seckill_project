package com.gz.seckill.rabbitmq;


import com.gz.seckill.VO.GoodsVO;
import com.gz.seckill.pojo.SeckillMessage;
import com.gz.seckill.pojo.SeckillOrder;
import com.gz.seckill.pojo.User;
import com.gz.seckill.service.IGoodsService;
import com.gz.seckill.service.IOrderService;
import com.gz.seckill.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

//消息接收者
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

//    @RabbitListener(queues = "seckillQueue")
//    public void receive(String message) {
//        log.info("接受消息：" + message);
//    }

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接受消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            return;
        }
        //判断是否重复购买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null) {
            return;
        }
        //下单
        orderService.secKill(user,goods);
    }
}
