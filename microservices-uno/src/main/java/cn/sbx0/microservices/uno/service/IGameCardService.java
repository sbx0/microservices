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

    List<CardEntity> getDiscardCards(String roomCode);

    void initGame(String roomCode);

    List<CardEntity> getCardsByUserId(String roomCode, Long id);

    boolean playCard(String roomCode, String uuid, String color, Long id);

    List<CardEntity> nextPlay(String roomCode, Long id);
}
