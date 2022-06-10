package cn.sbx0.microservices.home;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RedisTestSetup {
    @MockBean
    protected RedisTemplate<String, Object> redisTemplate;
    @MockBean
    protected StringRedisTemplate stringRedisTemplate;
    @MockBean
    protected ValueOperations valueOperations;
    @MockBean
    protected ListOperations listOperations;

    @BeforeEach
    void beforeEach() {
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
    }
}
