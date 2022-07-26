package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@JsonIgnoreProperties({"author", "assets"})
@Data
public class GitHubReleasesResponse {
    private String url;
    private String assets_url;
    private String upload_url;
    private String html_url;
    private Long id;
    private String node_id;
    private String tag_name;
    private String target_commitish;
    private String name;
    private Boolean draft;
    private Boolean prerelease;
    private String created_at;
    private String published_at;
    private String tarball_url;
    private String zipball_url;
    private String body;
}
