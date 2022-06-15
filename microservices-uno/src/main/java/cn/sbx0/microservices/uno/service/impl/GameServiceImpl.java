package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameInfoVO;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/5/27
 */
@Slf4j
@Service
public class GameServiceImpl implements IGameService {
    @Resource
    private IGameRoomService roomService;
    @Resource
    private IGameRoomUserService gamerService;
    @Resource
    private IGameCardService cardService;
    @Resource
    private AccountService accountService;

    @Override
    public ResponseVO<GameInfoVO> getInfoByCodeAndUserId(String code, Long userId) {
        GameInfoVO gameInfo = new GameInfoVO();
        gameInfo.setCurrentGamer(accountService.findById(userId));
        gameInfo.setRoomInfo(roomService.getInfoByUserId(code, userId));
        gameInfo.setCards(cardService.getCardsByUserId(code, userId));
        gameInfo.setGamers(gamerService.getGamerByCode(code));
        gameInfo.setDiscards(cardService.getDiscardCards(code));
        return ResponseVO.success(gameInfo);
    }
}
