package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.RealTimeEastMoneyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface RealTimeEastMoneyService {
    @GET("js/002963.js")
    Call<RealTimeEastMoneyResponse> getDetail(
            @Query("v") String v
    );
}
