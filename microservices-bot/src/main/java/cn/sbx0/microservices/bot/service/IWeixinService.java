package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.utils.AesException;

/**
 * @author sbx0
 * @since 2022/8/23
 */
public interface IWeixinService {
    String getAccessToken();

    String auth(String signature, String timestamp, String nonce, String echostr) throws AesException;
}
