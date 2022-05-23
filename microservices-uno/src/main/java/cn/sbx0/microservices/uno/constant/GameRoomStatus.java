package cn.sbx0.microservices.uno.constant;

import lombok.Getter;

/**
 * @author sbx0
 * @since 2022/3/21
 */
@Getter
public class GameRoomStatus {
    public static final int ENDING = 2;
    public static final int BEGINNING = 1;
    public static final int INITIAL = 0;
}
