package com.gz.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gz.seckill.VO.LoginVO;
import com.gz.seckill.VO.RespBean;
import com.gz.seckill.VO.RespBeanEnum;
import com.gz.seckill.exception.GlobalException;
import com.gz.seckill.mapper.UserMapper;
import com.gz.seckill.pojo.User;
import com.gz.seckill.utils.CookieUtil;
import com.gz.seckill.utils.MD5Util;
import com.gz.seckill.utils.UUIDUtil;
import com.gz.seckill.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2023-05-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements com.gz.seckill.service.IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 实现登录功能的Service层
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean dologin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        //校验 //已经用注解取代
/*        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)) {
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        User user = userMapper.selectById(mobile);
        if(null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if(!user.getPassword().equals(MD5Util.storageMD5(password,user.getSalt()))){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
                //生成cookie
        String ticket = UUIDUtil.uuid();
//        request.getSession().setAttribute(ticket, user);
        //将用户信息存入redis中,避免分布式session问题
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success();

//        return RespBean.success(ticket);
    }



    /***
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if(user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /***
     * 更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.frontendToStorageMD5(password, user.getSalt()));
        int i = userMapper.updateById(user);
        if(i == 1) {
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        } else {
            return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
        }
    }
}
