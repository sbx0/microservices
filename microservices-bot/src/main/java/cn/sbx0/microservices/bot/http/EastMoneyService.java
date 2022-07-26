package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.EastMoneyServiceResponse;
import cn.sbx0.microservices.bot.http.entity.GetFundNetDiagramResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface EastMoneyService {
    @GET("FundMApi/FundNetDiagram.ashx")
    Call<EastMoneyServiceResponse<List<GetFundNetDiagramResponse>>> getFundNetDiagram(
            @Query("FCODE") String code,
            @Query("RANGE") String range,
            @Query("deviceid") String deviceid,
            @Query("plat") String plat,
            @Query("product") String product,
            @Query("version") String version,
            @Query("_") String random
    );
}
