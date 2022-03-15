package cn.sbx0.microservices.account.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.account.AccountApplication;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.LoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/15
 */
@SpringBootTest(classes = AccountApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class AccountControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<LoginDTO> users = new ArrayList<>();
    @Autowired
    private MockMvc mockMvc;
    private String token = "";

    {
        users.add(new LoginDTO("sbx00", "test"));
        users.add(new LoginDTO("sbx01", "test"));
        users.add(new LoginDTO("sbx02", "test"));
        users.add(new LoginDTO("sbx03", "test"));
        users.add(new LoginDTO("sbx04", "test"));
    }

    @Test
    void register() throws Exception {
        String params = objectMapper.writeValueAsString(users.get(0));
        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals("false", response);
    }

    @Test
    void login() throws Exception {
        String params = objectMapper.writeValueAsString(users.get(0));
        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        SaTokenInfo saTokenInfo = objectMapper.readValue(response, SaTokenInfo.class);
        Assertions.assertNotNull(saTokenInfo);
        token = saTokenInfo.getTokenValue();
    }

    @Test
    void loginInfo() throws Exception {
        login();
        String response = mockMvc.perform(
                MockMvcRequestBuilders.get("/user/loginInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("satoken", token)
                        .header("version", "dev")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        AccountEntity account = objectMapper.readValue(response, AccountEntity.class);
        Assertions.assertNotNull(account);
    }
}