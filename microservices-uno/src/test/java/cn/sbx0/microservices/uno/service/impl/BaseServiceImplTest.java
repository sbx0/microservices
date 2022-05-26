package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.CardEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static cn.sbx0.microservices.uno.TestDataProvider.stpUtil;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@SuppressWarnings({"rawtypes", "unchecked", "SpringJavaAutowiredMembersInspection"})
@ExtendWith(SpringExtension.class)
@MockBean(classes = {RedisTemplate.class, StringRedisTemplate.class, ValueOperations.class, ListOperations.class, SetOperations.class})
public class BaseServiceImplTest {
    @Autowired
    protected RedisTemplate<String, CardEntity> redisTemplate;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    @Autowired
    protected ValueOperations valueOperations;
    @Autowired
    protected ListOperations listOperations;
    @Autowired
    protected SetOperations setOperations;

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

    @BeforeEach
    void beforeEach() {
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(stringRedisTemplate.opsForList()).willReturn(listOperations);
        given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(redisTemplate.opsForSet()).willReturn(setOperations);
    }

}
