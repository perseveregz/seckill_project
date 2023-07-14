package com.gz.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gz.seckill.VO.GoodsVO;
import com.gz.seckill.mapper.GoodsMapper;
import com.gz.seckill.pojo.Goods;
import com.gz.seckill.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2023-05-24
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /***
     * 返回商品列表
     * @return
     */
    @Override
    public List<GoodsVO> findGoodsVO() {
        return goodsMapper.findGoodsVO();
    }
//
    /***
     * 获取商品详情
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVO findGoodsVOByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVOByGoodsId(goodsId);
    }
}
