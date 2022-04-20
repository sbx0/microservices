package cn.sbx0.microservices.account.entity;


import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sbx0
 * @since 2022/4/19
 */
@Mapper
public interface AccountConverter {
    AccountConverter INSTANCE = Mappers.getMapper(AccountConverter.class);

    AccountVO entityToVO(AccountEntity source);
}
