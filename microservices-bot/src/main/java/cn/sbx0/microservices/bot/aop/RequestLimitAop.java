package cn.sbx0.microservices.bot.aop;

import cn.sbx0.microservices.bot.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangh
 * @since 2022/6/28
 */
@Slf4j
@Aspect
@Component
public class RequestLimitAop {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisScript<Long> limitScript;


    @Pointcut("@annotation(cn.sbx0.microservices.bot.aop.RequestLimit)")
    private void check() {

    }

    @Before("check()")
    public void before(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            // 如果没有登录，就用IP
            token = IpUtils.getIp(request);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequestLimit redisLimit = method.getAnnotation(RequestLimit.class);
        if (redisLimit != null) {
            String className = method.getDeclaringClass().getName();
            String name = method.getName();
            String limitKey = "requestLimit:" + DigestUtils.md5DigestAsHex((className + name + token).getBytes(StandardCharsets.UTF_8));
            long limit = redisLimit.permitsPerSecond();
            long expire = redisLimit.expire();
            List<String> keys = new ArrayList<>();
            keys.add(limitKey);
            Long count = stringRedisTemplate.execute(limitScript, keys, String.valueOf(expire), String.valueOf(limit));
            if (count != null && count == 0) {
                throw new RequestLimitException(redisLimit.msg());
            }
        }

    }

}
