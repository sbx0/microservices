package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.CardEntity;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-03-22
 */
public interface IGameCardService {
    void initCardDeck(String roomCode);

    List<CardEntity> drawCard(String roomCode);

    List<CardEntity> drawCard(String roomCode, int number);

    List<CardEntity> drawCard(String roomCode, Long userId, int number);

    List<CardEntity> myCardList(String roomCode);

    void discardCard(String roomCode, CardEntity card);

    List<CardEntity> discardCardList(String roomCode);

    void initGame(String roomCode);

    List<CardEntity> nextPlay(String roomCode);

    List<CardEntity> getCardListById(String roomCode, Long id);

    boolean playCard(String roomCode, String uuid, String color, Long id);

    List<CardEntity> botNextPlay(String roomCode, Long id);
}
