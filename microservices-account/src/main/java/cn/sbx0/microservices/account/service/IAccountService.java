package cn.sbx0.microservices.account.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

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

    ResponseVO<SaTokenInfo> login(LoginDTO dto);

    void logout();

    ResponseVO<Boolean> register(LoginDTO dto);

    AccountVO loginInfo(Long userId);

    AccountVO findByUserName(String name);

    Map<Long, String> mapNameByIds(IDsDTO dto);
}
