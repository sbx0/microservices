package cn.sbx0.microservices.account.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.IAccountService;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.LoginDTO;
import cn.sbx0.microservices.entity.ResponseVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * @author wangh
 * @since 2022/5/16
 */
@SuppressWarnings({"SpringJavaAutowiredMembersInspection"})
@MockBean(classes = {AccountMapper.class})
class AccountServiceImplTest extends BaseServiceImplTest {
    @Autowired
    private IAccountService service;
    @Autowired
    private AccountMapper mapper;

    @Test
    void findByUsername() {
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setUsername("test");
        account.setNickname("test");
        given(mapper.findByUsername(anyString())).willReturn(account);
        AccountEntity result = service.findByUsername(account.getUsername());
        assertEquals(account.getId(), result.getId());
        assertEquals(account.getNickname(), result.getNickname());
    }

    @Test
    void login() {
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setUsername("test");
        account.setNickname("test");
        account.setPassword("test");
        given(mapper.findByUsername(anyString())).willReturn(account);
        LoginDTO dto = new LoginDTO();
        dto.setUsername("test");
        dto.setPassword("test");
        ResponseVO<SaTokenInfo> response = service.login(dto);
        assertNotNull(response);
        assertEquals(ResponseVO.SUCCESS, response.getCode());
        assertNotNull(response.getData());
        assertEquals(0L, response.getData().getLoginId());
    }

    @Test
    void logout() {
        service.logout();
    }

    @Test
    void register() {
        findByUserName();
        LoginDTO dto = new LoginDTO();
        dto.setUsername("test1");
        dto.setPassword("test1");
        given(mapper.insert(any())).willReturn(1);
        ResponseVO<Boolean> response = service.register(dto);
        assertNotNull(response);
        assertEquals(ResponseVO.SUCCESS, response.getCode());
        assertNotNull(response.getData());
        assertTrue(response.getData());
    }

    @Test
    void loginInfo() {
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setUsername("test");
        account.setNickname("test");
        account.setPassword("test");
        given(mapper.selectById(any())).willReturn(account);
        AccountVO response = service.loginInfo(account.getId());
        assertNotNull(response);
        assertEquals(account.getNickname(), response.getNickname());
    }

    @Test
    void findByUserName() {
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setUsername("test");
        account.setNickname("test");
        account.setPassword("test");
        given(mapper.findByUsername(account.getUsername())).willReturn(account);
        AccountVO response = service.findByUserName(account.getUsername());
        assertNotNull(response);
        assertEquals(account.getNickname(), response.getNickname());
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public IAccountService service() {
            return new AccountServiceImpl();
        }
    }
}
