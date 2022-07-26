package cn.sbx0.microservices.bot.service;


import cn.sbx0.microservices.bot.entity.MessageEntity;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface IMessageService {
    void sendMessage(MessageEntity data);
}
