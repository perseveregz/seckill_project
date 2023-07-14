package com.gz.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gz.seckill.VO.GoodsVO;
import com.gz.seckill.VO.RespBean;
import com.gz.seckill.VO.RespBeanEnum;
import com.gz.seckill.exception.GlobalException;
import com.gz.seckill.pojo.Order;
import com.gz.seckill.pojo.SeckillMessage;
import com.gz.seckill.pojo.SeckillOrder;
import com.gz.seckill.pojo.User;
import com.gz.seckill.rabbitmq.MQSender;
import com.gz.seckill.service.IGoodsService;
import com.gz.seckill.service.IOrderService;
import com.gz.seckill.service.ISeckillOrderService;
import com.gz.seckill.utils.JsonUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/secKill")
@Slf4j
public class SecKillController implements InitializingBean {
//public class SecKillController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

//    内存标记
    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

//    /**
//     * 秒杀
//     * Windows 优化前 qps 402
//     * Linux   优化前 qps 105
//     * @param model
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @RequestMapping("/doSecKill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//        //判断用户
//        if(user == null) return "login";
//        model.addAttribute("user", user);
//        //判断库存
//        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
//        if(goods.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK_ERROR.getMessage());
//            return "secKillFail";
//        }
//        //判断订单(是否重复抢购)
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if(seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail";
//        }
//        //抢购
//        Order order = orderService.secKill(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "orderDetail";
//    }

    /**
     * 秒杀
     * Windows 优化前 qps 402
     * Linux   优化前 qps 105
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
//    @PostMapping("/doSecKill")
//    @ResponseBody
//    public RespBean doSecKill(Model model, User user, Long goodsId) {
//        //判断用户
//        if(user == null)
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        //判断库存
//        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
//        if(goods.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
//        }
//        //判断订单(是否重复抢购)
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if(seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//        //抢购
//        Order order = orderService.secKill(user, goods);
//        return RespBean.success(order);
//    }

    @PostMapping("/doSecKill")
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId) {
        //判断用户
        if(user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        //判断库存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断订单(是否重复抢购)
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("oreder:"+user.getId() + ":" + goodsId);
        if(seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
//        内存标记，减少redis访问
        if(emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
        }
        //预减库存
        Long stock = valueOperations.decrement("seckillGoods" + goodsId);
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
        }
        //抢购
//        Order order = orderService.secKill(user, goods);
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.send(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
//        return null;
    }

//    /***
//     * 秒杀
//     * Windows 优化前 qps 402
//     * Linux   优化前 qps 105
//     * Windows 缓存后 qps 936
//     * Windows 缓存后 qps 2629
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
//    @ResponseBody
//    public RespBean doSecKill( @PathVariable String path, User user, Long goodsId) {
//        //判断用户
//        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        //判断库存
//        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
//        if(goods.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
//        }
//        //判断订单(是否重复抢购)
//        //SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        if(seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//        //抢购
//        Order order = orderService.secKill(user, goods);
//        return RespBean.success(order);
//
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //秒杀路径校验
//        boolean check = orderService.checkPath(user, goodsId, path);
//        if(!check) {
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }
//        //判断是否重复
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        if(seckillOrder != null) {
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//        //内存标记，减少redis访问
//        if(emptyStockMap.get(goodsId)) {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
//        }
//        //预减库存
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        if(stock < 0) {
//            emptyStockMap.put(goodsId, true);
//            valueOperations.increment("seckillGoods:" + goodsId);
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
//        }
//        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
//        return RespBean.success(0);
//    }

    /***
     * 获取秒杀结果
     * @param user
     * @param goodsId orderId成功 -1失败 0排队中
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }
//
//    /***
//     * 获取秒杀地址
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @AccessLimit(second=5, maxCount=5, needLogin=true)
//    @RequestMapping(value = "/path", method = RequestMethod.GET)
//    @ResponseBody
//    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
//        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        //访问限流，5s内访问5次
//       /* ValueOperations  valueOperations= redisTemplate.opsForValue();
//        String uri = request.getRequestURI();
//        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
//        if(count == null) {
//            valueOperations.set(uri + ":" + user.getId(), 1, 5, TimeUnit.SECONDS);
//        } else if(count < 5) {
//            valueOperations.increment(uri + ":" + user.getId());
//        } else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }*/
//        //验证码校验
//        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
//        if(!check) return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
//        //创建验证码
//        String str = orderService.createPath(user,goodsId);
//        return RespBean.success(str);
//    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null==user||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成数字类型的验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }

    /***
     * 系统初始化时执行的方法
     * 把商品加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> list = goodsService.findGoodsVO();
        if(CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVO ->{
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVO.getId(), goodsVO.getStockCount());
            emptyStockMap.put(goodsVO.getId(), false);
        });
    }
}
