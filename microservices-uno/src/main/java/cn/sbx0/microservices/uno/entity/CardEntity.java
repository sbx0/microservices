package cn.sbx0.microservices.uno.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author sbx0
 * @since 2022/3/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String color;
    private String point;
}