package cn.sbx0.microservices.account.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.LoginDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-03-04
 */
public interface IAccountService extends IService<AccountEntity> {
    AccountEntity findByUsername(String username);

    SaTokenInfo login(LoginDTO dto);

    void logout();

    boolean register(LoginDTO dto);

    AccountVO loginInfo();
}
