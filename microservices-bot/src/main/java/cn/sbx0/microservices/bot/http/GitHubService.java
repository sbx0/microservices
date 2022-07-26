package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.GitHubReleasesResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface GitHubService {
    @GET("/repos/xiaoye97/DinkumChinese/releases/latest")
    Call<GitHubReleasesResponse> getDinkumChineseLatest();
}
