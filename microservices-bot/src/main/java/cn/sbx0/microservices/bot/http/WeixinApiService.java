package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.MessageTemplateSendDTO;
import cn.sbx0.microservices.bot.http.entity.WeixinGetAccessTokenBody;
import cn.sbx0.microservices.bot.http.entity.WeixinMessageTemplateSendBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author sbx0
 * @since 2022/8/24
 */
public interface WeixinApiService {
    @GET("/cgi-bin/token")
    Call<WeixinGetAccessTokenBody> getAccessToken(
            @Query("grant_type") String grantType,
            @Query("appid") String appid,
            @Query("secret") String secret
    );

    @POST("/cgi-bin/message/template/send")
    Call<WeixinMessageTemplateSendBody> messageTemplateSend(
            @Query("access_token") String accessToken,
            @Body MessageTemplateSendDTO message
    );
}
