package cn.sbx0.microservices.bot.aop;

/**
 * @author wangh
 * @since 2022/6/28
 */
public class RequestLimitException extends RuntimeException {
    public RequestLimitException(String msg) {
        super(msg);
    }
}
