package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.config.RetrofitConfig;
import cn.sbx0.microservices.bot.http.entity.WeixinGetAccessTokenBody;
import cn.sbx0.microservices.bot.utils.AesException;
import cn.sbx0.microservices.bot.utils.SHA1Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author sbx0
 * @since 2022/8/23
 */
@Slf4j
@Service
public class WeixinServiceImpl implements IWeixinService {
    public static final String ACCESS_TOKEN_CACHE_KEY = "weixin:access_token";
    @Value("${weixin.auth.token}")
    private String token;
    @Value("${weixin.app-id}")
    private String appId;
    @Value("${weixin.app-secret}")
    private String appSecret;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RetrofitConfig retrofitConfig;

    @Override
    public String getAccessToken() {
        String accessToken = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_CACHE_KEY);
        if (!StringUtils.hasText(accessToken)) {
            Call<WeixinGetAccessTokenBody> call = retrofitConfig.weixinApiService.getAccessToken("client_credential", appId, appSecret);
            try {
                Response<WeixinGetAccessTokenBody> execute = call.execute();
                if (execute.isSuccessful()) {
                    WeixinGetAccessTokenBody body = execute.body();
                    if (body != null) {
                        accessToken = body.getAccessToken();
                        Long expiresIn = body.getExpiresIn();
                        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_CACHE_KEY, accessToken);
                        stringRedisTemplate.expire(ACCESS_TOKEN_CACHE_KEY, expiresIn - 30, TimeUnit.SECONDS);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return accessToken;
    }

    @Override
    public String auth(String signature, String timestamp, String nonce, String echostr) throws AesException {
        String sha1 = SHA1Utils.getSHA1(token, timestamp, nonce);
        return signature.equals(sha1) ? echostr : null;
    }
}
