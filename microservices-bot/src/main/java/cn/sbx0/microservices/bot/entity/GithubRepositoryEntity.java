package cn.sbx0.microservices.bot.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/8/2
 */
@Data
public class GithubRepositoryEntity {
    private String author;
    private String name;

    public GithubRepositoryEntity() {
    }

    public GithubRepositoryEntity(String author, String name) {
        this.author = author;
        this.name = name;
    }
}
