package cn.sbx0.microservices.uno.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/5/11
 */
@Data
public class QueueInfoVO {
    private int size;
    private boolean join;
}
