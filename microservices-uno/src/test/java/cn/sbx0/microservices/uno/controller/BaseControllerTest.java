package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static cn.sbx0.microservices.uno.TestDataProvider.stpUtil;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
abstract class BaseControllerTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    protected MockMvc mvc;

    @BeforeAll
    static void beforeAll() {
        stpUtil = mockStatic(StpUtil.class);
        stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtil.when(StpUtil::getLoginIdAsString).thenReturn("0");
    }

    @AfterAll
    static void afterAll() {
        stpUtil.close();
    }

}



