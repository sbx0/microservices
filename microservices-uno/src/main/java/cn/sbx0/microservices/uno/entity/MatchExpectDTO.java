package cn.sbx0.microservices.uno.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchExpectDTO {
    private Long userId;
    private Integer gamerSize;
    private Boolean allowBot;
}
