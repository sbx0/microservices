package cn.sbx0.microservices.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@Getter
@Setter
public class QueryOrderDTO {
    private String field;
    private String sort;
}
