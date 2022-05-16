package cn.sbx0.microservices.account.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.impl.AccountServiceImpl;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.LoginDTO;
import cn.sbx0.microservices.entity.ResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/3/15
 */
@MockBean(classes = {AccountServiceImpl.class, AccountMapper.class})
@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest extends BaseControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccountServiceImpl service;

    @Test
    void login() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("test");
        loginDTO.setPassword("test");

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setUsername("test");
        accountEntity.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));

        SaTokenInfo saTokenInfo = new SaTokenInfo();
        saTokenInfo.setTokenName("token");
        saTokenInfo.setTokenValue("token");
        saTokenInfo.setIsLogin(true);
        saTokenInfo.setLoginId(1);
        saTokenInfo.setLoginType("test");

        given(service.login(BDDMockito.any())).willReturn(new ResponseVO<>(ResponseVO.SUCCESS, saTokenInfo));

        String response = mvc.perform(post("/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        assertTrue(StringUtils.hasText(response));
    }

    @Test
    void logout() throws Exception {
        String response = mvc.perform(post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertFalse(StringUtils.hasText(response));
    }

    @Test
    void register() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("test");
        loginDTO.setPassword("test");

        given(service.register(any())).willReturn(new ResponseVO<>(ResponseVO.SUCCESS, true));


        String response = mvc.perform(post("/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        assertTrue(StringUtils.hasText(response));
    }

    @Test
    void loginInfo() throws Exception {
        AccountVO account = new AccountVO();
        account.setUsername("test");
        given(service.loginInfo(anyLong())).willReturn(account);

        String response = mvc.perform(get("/loginInfo")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andReturn().getResponse().getContentAsString();

        assertTrue(StringUtils.hasText(response));
    }

    @Test
    void findByUserName() throws Exception {
        AccountVO account = new AccountVO();
        account.setId(1L);
        account.setUsername("test");
        given(service.findByUserName("test")).willReturn(account);

        String response = mvc.perform(get("/findByUserName?name=" + account.getUsername())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(account.getId()))
                .andExpect(jsonPath("username").value(account.getUsername()))
                .andReturn().getResponse().getContentAsString();

        assertTrue(StringUtils.hasText(response));
    }

    @Test
    void findById() throws Exception {
        AccountVO account = new AccountVO();
        account.setId(1L);
        account.setUsername("test");
        given(service.loginInfo(anyLong())).willReturn(account);

        String response = mvc.perform(get("/findById?id=" + account.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(account.getId()))
                .andExpect(jsonPath("username").value(account.getUsername()))
                .andReturn().getResponse().getContentAsString();

        assertTrue(StringUtils.hasText(response));
    }

}
