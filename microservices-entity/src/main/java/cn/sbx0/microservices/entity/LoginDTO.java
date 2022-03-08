package cn.sbx0.microservices.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sbx0
 * @since 2022/3/5
 */
@Getter
@Setter
public class LoginDTO {
    private String username;
    private String password;
}
