package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.CardEntity;

import java.io.Serializable;
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

    List<CardEntity> drawCard(String roomCode, Serializable userId, int number);

    List<CardEntity> myCardList(String roomCode);

    Boolean playCard(String roomCode, String uuid);

    void discardCard(String roomCode, CardEntity card);

    List<CardEntity> discardCardList(String roomCode);
}
