package cn.sbx0.microservices.uno.logic;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.RedisTestSetup;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@SuppressWarnings({"unchecked", "rawtypes", "SpringJavaAutowiredMembersInspection"})
@ExtendWith(SpringExtension.class)
class BasicGameRuleTest extends RedisTestSetup {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    @Autowired
    private BasicGameRule gameRule;
    @MockBean
    private IGameRoomUserService userService;
    @MockBean
    private IMessageService messageService;
    @MockBean
    private RandomBot randomBot;

    @BeforeAll
    static void beforeAll() {
        for (int i = 0; i < 6; i++) {
            AccountVO account = new AccountVO();
            account.setId((long) i);
            account.setUsername("username" + i);
            account.setNickname("nickname" + i);
            account.setNumberOfCards(10);
            account.setEmail("email" + i + "@sbx0.cn");
            GAMERS.add(account);
        }
    }

    @Test
    void getCurrentGamer() {
        given(valueOperations.get(anyString())).willReturn("1");
        given(userService.listByGameRoom(anyString())).willReturn(GAMERS);

        AccountVO currentGamer = gameRule.getCurrentGamer(ROOM_CODE);
        assertNotNull(currentGamer);
    }

    @Test
    void canIPlayNow() {
        getCurrentGamer();
        boolean result = gameRule.canIPlayNow(ROOM_CODE, USER_ID);
        assertTrue(result);
    }

    @Test
    void judgeIsCanPlay() {

    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public BasicGameRule service() {
            return new BasicGameRule();
        }
    }
}
