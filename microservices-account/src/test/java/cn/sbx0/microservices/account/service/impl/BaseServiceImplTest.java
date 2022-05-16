package cn.sbx0.microservices.account.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static cn.sbx0.microservices.account.TestDataProvider.bcrypt;
import static cn.sbx0.microservices.account.TestDataProvider.stpUtil;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@SuppressWarnings({"rawtypes", "unchecked", "SpringJavaAutowiredMembersInspection"})
@ExtendWith(SpringExtension.class)
@MockBean(classes = {RedisTemplate.class, StringRedisTemplate.class, ValueOperations.class, ListOperations.class})
public class BaseServiceImplTest {
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    @Autowired
    protected ValueOperations valueOperations;
    @Autowired
    protected ListOperations listOperations;

    @BeforeAll
    static void beforeAll() {
        stpUtil = mockStatic(StpUtil.class);
        stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtil.when(StpUtil::getLoginIdAsString).thenReturn("0");
        SaTokenInfo saTokenInfo = new SaTokenInfo();
        saTokenInfo.setLoginId(0L);
        stpUtil.when(StpUtil::getTokenInfo).thenReturn(saTokenInfo);

        bcrypt = mockStatic(BCrypt.class);
        bcrypt.when(() -> BCrypt.checkpw(anyString(), anyString())).thenReturn(true);
    }

    @AfterAll
    static void afterAll() {
        stpUtil.close();
    }

    @BeforeEach
    void beforeEach() {
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(stringRedisTemplate.opsForList()).willReturn(listOperations);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
    }

}
