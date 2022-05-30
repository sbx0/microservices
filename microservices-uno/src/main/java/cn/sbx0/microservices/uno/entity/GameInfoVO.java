package cn.sbx0.microservices.uno.entity;

import cn.sbx0.microservices.entity.AccountVO;
import lombok.Data;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/5/27
 */
@Data
public class GameInfoVO {
    private GameRoomInfoVO roomInfo;
    private AccountVO currentGamer;
    private List<AccountVO> gamers;
    private List<CardEntity> cards;
    private List<CardEntity> discards;
}
