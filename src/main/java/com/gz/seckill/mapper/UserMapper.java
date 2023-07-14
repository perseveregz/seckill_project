package com.gz.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gz.seckill.pojo.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2023-05-19
 */
public interface UserMapper extends BaseMapper<User> {

    public User selectById();
}
