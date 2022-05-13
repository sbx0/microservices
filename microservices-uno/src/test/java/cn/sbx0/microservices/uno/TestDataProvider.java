package cn.sbx0.microservices.uno;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.constant.CardColor;
import cn.sbx0.microservices.uno.constant.CardPoint;
import cn.sbx0.microservices.uno.entity.CardEntity;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sbx0
 * @since 2022/5/13
 */
public class TestDataProvider {
    public static final String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    public final static List<CardEntity> CARDS = new ArrayList<>();
    public final static String[] UUIDS = new String[24];
    public final static String[] POINTS = {
            CardPoint.ONE, CardPoint.TWO, CardPoint.ONE, CardPoint.TWO, CardPoint.ONE, CardPoint.TWO, CardPoint.ONE, CardPoint.TWO,
            CardPoint.REVERSE, CardPoint.REVERSE, CardPoint.REVERSE, CardPoint.REVERSE,
            CardPoint.SKIP, CardPoint.SKIP, CardPoint.SKIP, CardPoint.SKIP,
            CardPoint.DRAW_TWO, CardPoint.DRAW_TWO, CardPoint.DRAW_TWO, CardPoint.DRAW_TWO,
            CardPoint.WILD_DRAW_FOUR, CardPoint.WILD_DRAW_FOUR, CardPoint.WILD_DRAW_FOUR, CardPoint.WILD_DRAW_FOUR
    };
    public final static String[] COLORS = {
            CardColor.RED, CardColor.RED, CardColor.YELLOW, CardColor.YELLOW, CardColor.BLUE, CardColor.BLUE, CardColor.GREEN, CardColor.GREEN,
            CardColor.RED, CardColor.YELLOW, CardColor.BLUE, CardColor.GREEN,
            CardColor.RED, CardColor.YELLOW, CardColor.BLUE, CardColor.GREEN,
            CardColor.RED, CardColor.YELLOW, CardColor.BLUE, CardColor.GREEN,
            CardColor.RED, CardColor.YELLOW, CardColor.BLUE, CardColor.GREEN
    };
    public static MockedStatic<StpUtil> stpUtil;

    static {
        for (int i = 0; i < 6; i++) {
            AccountVO account = new AccountVO();
            account.setId((long) i);
            account.setUsername("username" + i);
            account.setNickname("nickname" + i);
            account.setNumberOfCards(10);
            account.setEmail("email" + i + "@sbx0.cn");
            GAMERS.add(account);
        }
        for (int i = 0; i < 24; i++) {
            CardEntity card = new CardEntity();
            UUIDS[i] = UUID.randomUUID().toString();
            card.setUuid(UUIDS[i]);
            card.setPoint(POINTS[i]);
            card.setColor(COLORS[i]);
            card.setUserId(0L);
            CARDS.add(card);
        }
    }
}
