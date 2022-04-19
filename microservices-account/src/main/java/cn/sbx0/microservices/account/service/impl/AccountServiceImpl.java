package cn.sbx0.microservices.account.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.IAccountService;
import cn.sbx0.microservices.entity.AccountConverter;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.LoginDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-04
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountEntity> implements IAccountService {
    @Resource
    private AccountConverter accountEntityConverter;

    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("test", BCrypt.gensalt()));
    }

    @Override
    public AccountEntity findByUsername(String username) {
        return getBaseMapper().findByUsername(username);
    }

    @Override
    public SaTokenInfo login(LoginDTO dto) {
        AccountEntity account = getBaseMapper().findByUsername(dto.getUsername());
        if (account != null && account.getId() > 0) {
            if (BCrypt.checkpw(dto.getPassword(), account.getPassword())) {
                StpUtil.login(account.getId());
                return StpUtil.getTokenInfo();
            }
        }
        return null;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public boolean register(LoginDTO dto) {
        AccountEntity exist = findByUsername(dto.getUsername());
        if (exist != null) {
            return false;
        }
        exist = new AccountEntity();
        exist.setUsername(dto.getUsername());
        exist.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        return getBaseMapper().insert(exist) > 0;
    }

    @Override
    @Cacheable(cacheNames = "loginInfo", key = "#userId")
    public AccountVO loginInfo(Long userId) {
        return accountEntityConverter.entityToVO(getById(userId));
    }
}
