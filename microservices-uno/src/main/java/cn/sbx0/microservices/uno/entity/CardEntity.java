package cn.sbx0.microservices.uno.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author sbx0
 * @since 2022/3/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardEntity {
    private String color;
    private String point;

    @Override
    public String toString() {
        return "color='" + color + '\'' +
                ", point='" + point + '\'';
    }
}