package cn.sbx0.microservices.uno.constant;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sbx0
 * @since 2022/5/20
 */
class GameRedisKeyTest {
    public static final String uuid = UUID.randomUUID().toString();
    public static final String userId = "123456789";

    @Test
    void test1() {
        String key = GameRedisKey.ROOM_CARDS.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test2() {
        String key = GameRedisKey.ROOM_DISCARDS.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test3() {
        String key = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test4() {
        String key = GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test5() {
        String key = GameRedisKey.ROOM_DRAW.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test6() {
        String key = GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, uuid);
        assertTrue(key.contains(uuid));
    }

    @Test
    void test7() {
        String key = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, uuid)
                .replaceAll(GameRedisKey.USER_ID, userId);
        assertTrue(key.contains(uuid));
        assertTrue(key.contains(userId));
    }

}
