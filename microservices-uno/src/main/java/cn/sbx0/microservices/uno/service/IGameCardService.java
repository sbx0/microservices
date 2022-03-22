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

    List<CardEntity> drawCardOnBeginning(String roomCode);

    List<CardEntity> myCard(String roomCode);
}
