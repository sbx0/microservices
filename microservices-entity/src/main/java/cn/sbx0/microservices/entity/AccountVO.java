package cn.sbx0.microservices.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wangh
 * @since 2022-03-04
 */
@Getter
@Setter
public class AccountVO implements Serializable {
    private static final long serialVersionUID = -1;

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private Integer numberOfCards;

}
