package cn.sbx0.microservices.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author sbx0
 * @since 2022/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IDsDTO {
    private Set<Long> ids;
}
