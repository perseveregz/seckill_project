package com.gz.seckill.controller;


import com.gz.seckill.VO.OrderDetailVO;
import com.gz.seckill.VO.RespBean;
import com.gz.seckill.VO.RespBeanEnum;
import com.gz.seckill.pojo.User;
import com.gz.seckill.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2023-05-24
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /***
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        OrderDetailVO orderDetailVO = orderService.detail(orderId);
        return RespBean.success(orderDetailVO);
    }
}
