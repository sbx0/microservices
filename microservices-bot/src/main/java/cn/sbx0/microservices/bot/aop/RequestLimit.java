package cn.sbx0.microservices.bot.aop;

import java.lang.annotation.*;

/**
 * @author wangh
 * @since 2022/6/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RequestLimit {
    /**
     * 最多的访问限制次数
     */
    long permitsPerSecond() default 2;

    /**
     * 过期时间也可以理解为单位时间，单位秒，默认60
     */
    long expire() default 60;


    /**
     * 得不到令牌的提示语
     */
    String msg() default "您操作的太快了，请稍后再试。";
}
