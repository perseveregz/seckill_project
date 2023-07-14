package com.gz.seckill.controller;

import com.gz.seckill.VO.LoginVO;
import com.gz.seckill.VO.RespBean;
import com.gz.seckill.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Auther:Mr.Guo
 * @create:2023/7/5-22:21
 * @VERSON:1.8
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class loginController {

    @Autowired
    private IUserService userService;
    /***
     * 跳转登陆页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    /***
     * 登录功能：和前端进行交互，拿到了输入的手机号和密码参数
     * @param loginVO
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody//传参注解
    public RespBean doLogin(@Valid LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {

        return userService.dologin(loginVO, request, response);
    }

}
