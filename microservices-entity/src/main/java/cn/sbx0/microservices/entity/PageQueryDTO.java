package cn.sbx0.microservices.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@Getter
@Setter
public class PageQueryDTO {
    private Integer page;
    private Integer size;
    private String keywords;

    public PageQueryDTO() {
        this.page = 1;
        this.size = 10;
    }
}
