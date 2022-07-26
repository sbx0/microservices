package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.http.entity.GitHubReleasesResponse;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface IGitHubBotService {
    void readData();

    String handleData(GitHubReleasesResponse data);
}
