package cn.sbx0.microservices.account.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.account.entity.AccountConverter;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.IAccountService;
import cn.sbx0.microservices.entity.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("test", BCrypt.gensalt()));
    }

    @Override
    public AccountEntity findByUsername(String username) {
        return getBaseMapper().findByUsername(username);
    }

    @Override
    public ResponseVO<SaTokenInfo> login(LoginDTO dto) {
        AccountEntity account = getBaseMapper().findByUsername(dto.getUsername());
        if (account != null && account.getId() > 0) {
            if (BCrypt.checkpw(dto.getPassword(), account.getPassword())) {
                StpUtil.login(account.getId());
                return ResponseVO.success(StpUtil.getTokenInfo());
            } else {
                return ResponseVO.failed(null, "wrong password");
            }
        }
        return ResponseVO.failed(null, "account not found");
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public ResponseVO<Boolean> register(LoginDTO dto) {
        AccountEntity exist = findByUsername(dto.getUsername());
        if (exist != null) {
            return ResponseVO.failed(null, "account exist!");
        }
        exist = new AccountEntity();
        exist.setUsername(dto.getUsername());
        exist.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        boolean result = getBaseMapper().insert(exist) > 0;
        return ResponseVO.judge(result, result);
    }

    @Override
    public AccountVO loginInfo(Long userId) {
        AccountEntity entity = getById(userId);
        return AccountConverter.INSTANCE.entityToVO(entity);
    }

    @Override
    public AccountVO findByUserName(String name) {
        AccountEntity entity = getBaseMapper().findByUsername(name);
        return AccountConverter.INSTANCE.entityToVO(entity);
    }

    @Override
    public Map<Long, String> mapNameByIds(IDsDTO dto) {
        Map<Long, String> cache = new HashMap<>();
        Set<Long> ids = dto.getIds();
        for (Long id : ids) {
            AccountEntity account = getBaseMapper().selectById(id);
            if (account != null) {
                cache.put(id, account.getUsername());
            } else {
                cache.put(id, "not found!");
            }
        }
        return cache;
    }
}
