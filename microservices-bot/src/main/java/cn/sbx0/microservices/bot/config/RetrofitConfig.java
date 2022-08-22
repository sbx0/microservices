package cn.sbx0.microservices.bot.config;

import cn.sbx0.microservices.bot.converter.JsonpConverterFactory;
import cn.sbx0.microservices.bot.http.DingDingService;
import cn.sbx0.microservices.bot.http.EastMoneyService;
import cn.sbx0.microservices.bot.http.GitHubService;
import cn.sbx0.microservices.bot.http.RealTimeEastMoneyService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.PostConstruct;

/**
 * @author wangh
 * @since 2022/6/23
 */
@Component
public class RetrofitConfig {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public EastMoneyService eastMoneyService;
    public RealTimeEastMoneyService realTimeEastMoneyService;
    public DingDingService dingDingService;
    public GitHubService gitHubService;
    @Value("${east-money.base-url}")
    private String eastMoneyBaseUrl;
    @Value("${real-time-east-money.base-url}")
    private String realTimeEastMoneyBaseUrl;

    @PostConstruct
    public void init() {
        eastMoneyService = new Retrofit.Builder()
                .baseUrl(eastMoneyBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .build()
                .create(EastMoneyService.class);
        realTimeEastMoneyService = new Retrofit.Builder()
                .baseUrl(realTimeEastMoneyBaseUrl)
                .addConverterFactory(JsonpConverterFactory.create(OBJECT_MAPPER))
                .build()
                .create(RealTimeEastMoneyService.class);
        dingDingService = new Retrofit.Builder()
                .baseUrl("https://oapi.dingtalk.com")
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .build()
                .create(DingDingService.class);
        gitHubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .build()
                .create(GitHubService.class);
    }
}
