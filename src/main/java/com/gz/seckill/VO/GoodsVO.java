package com.gz.seckill.VO;

import com.gz.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

//商品返回对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class  GoodsVO extends Goods {

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;
}
