package com.gz.seckill.controller;



import com.gz.seckill.VO.RespBean;
import com.gz.seckill.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /*
     * 测试发送RabbitMQ消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("Hello");
    }

    /***
     * 用户信息测试
     * @return
     */
    @RequestMapping("info")
    @ResponseBody
    public RespBean info() {
        return RespBean.success();
    }



    /*
     * 测试发送RabbitMQ消息
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq02() {
        mqSender.send("Hello");
    }

    /*
     * 测试发送RabbitMQ消息
     */
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq03() {
        mqSender.send("Hello,Red");
    }

    /*
     * 测试发送RabbitMQ消息
     */
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq04() {
        mqSender.send("Hello,Green");
    }
}
