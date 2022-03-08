package cn.sbx0.microservices.account.mapper;


import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.LoginDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-03-04
 */
public interface AccountMapper extends BaseMapper<AccountEntity> {
    AccountEntity findByUsername(@Param(value = "username") String username);

    AccountEntity findByUsernameAndPassword(LoginDTO dto);
}
