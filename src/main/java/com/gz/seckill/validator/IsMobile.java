package com.gz.seckill.validator;

//import com.gz.seckill.VO.IsMobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注解的格式可以从其他注解那里复制过来，然后写上自定义的内容
 */
@Target({METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER,TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {
    //要求手机号码是必填的
    boolean required() default true;
    String message() default "手机号码格式错误";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
