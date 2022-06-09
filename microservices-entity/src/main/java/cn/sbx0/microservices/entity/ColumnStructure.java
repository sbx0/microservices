package cn.sbx0.microservices.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/6/9
 */
@Data
public class ColumnStructure {
    private String columnName;
    private String columnType;
    private String columnComment;
}
