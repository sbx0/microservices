package cn.sbx0.microservices.entity;


import org.mapstruct.Mapper;

/**
 * @author sbx0
 * @since 2022/4/19
 */
@Mapper(componentModel = "spring")
public interface AccountConverter {
    AccountVO entityToVO(AccountEntity source);
}
