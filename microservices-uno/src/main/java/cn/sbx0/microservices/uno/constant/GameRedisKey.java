package cn.sbx0.microservices.uno.constant;

/**
 * @author sbx0
 * @since 2022/5/7
 */
public class GameRedisKey {
    public static final String ROOM_CODE = "\\{room_code\\}";
    public static final String USER_ID = "\\{user_id\\}";
    public static final String ROOM_CARDS = "game:" + ROOM_CODE + ":cards:room";
    public static final String ROOM_DISCARDS = "game:" + ROOM_CODE + ":cards:discards";
    public static final String ROOM_PENALTY = "game:" + ROOM_CODE + ":cards:penalty";
    public static final String ROOM_DIRECTION = "game:" + ROOM_CODE + ":cards:direction";
    public static final String ROOM_DRAW = "game:" + ROOM_CODE + ":cards:draw";
    public static final String CURRENT_GAMER = "game:" + ROOM_CODE + ":current";
    public static final String USER_CARDS = "game:" + ROOM_CODE + ":cards:user:" + USER_ID;
}
