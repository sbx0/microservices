package cn.sbx0.microservices.bot.http;

import cn.sbx0.microservices.bot.http.entity.GitHubReleasesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface GitHubService {
    @GET("/repos/{user}/{repositoryName}/releases/latest")
    Call<GitHubReleasesResponse> getDinkumChineseLatest(
            @Path("user") String user,
            @Path("repositoryName") String repositoryName
    );
}
