package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.SendRobotMessageBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface DingDingService {
    @POST("/robot/send")
    Call<Void> sendRobotMessage(
            @Query("access_token") String accessToken,
            @Body SendRobotMessageBody data
    );
}
