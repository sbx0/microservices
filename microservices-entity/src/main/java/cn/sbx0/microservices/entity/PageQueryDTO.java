package cn.sbx0.microservices.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@Getter
@Setter
public class PageQueryDTO {
    private Integer page;
    private Integer size;
    private String keyword;
    private List<QueryOrderDTO> orders;
    private List<QueryFilterDTO> filters;

    public PageQueryDTO() {
        this.page = 1;
        this.size = 20;
        this.orders = Collections.emptyList();
        this.filters = Collections.emptyList();
    }
}
