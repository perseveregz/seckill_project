package com.gz.seckill.exception;

import com.gz.seckill.VO.RespBean;
import com.gz.seckill.VO.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


//全局异常处理类
//该注解返回的就是一个@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {
    //@ExceptionHandler里面写的就是自己需要处理的异常类
    @ExceptionHandler(Exception.class)
    public RespBean exceptionHandler(Exception e) {
        if(e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            return RespBean.error(globalException.getRespBeanEnum());
        } else if(e instanceof BindException) {//这个异常是使用validation参数校验时出现抛出信息用的
            BindException bindException = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            //获取所有的异常信息
            respBean.setMessage(respBean.getMessage() + "：" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        } else {
            return RespBean.error(RespBeanEnum.ERR0R);
        }
    }
}
