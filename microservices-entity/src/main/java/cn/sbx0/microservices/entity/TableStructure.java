package cn.sbx0.microservices.entity;

import lombok.Data;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/6/9
 */
@Data
public class TableStructure {
    private String tableName;
    private List<ColumnStructure> columns;
}
