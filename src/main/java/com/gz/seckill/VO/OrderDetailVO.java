package com.gz.seckill.VO;


import com.gz.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//订单详情返回对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {

    private Order order;

    private GoodsVO goodsVO;
}
