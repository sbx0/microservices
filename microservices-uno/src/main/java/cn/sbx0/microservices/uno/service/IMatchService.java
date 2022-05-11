package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;

/**
 * @author sbx0
 * @since 2022/5/10
 */
public interface IMatchService {
    // join match queue
    ResponseVO<Boolean> join(MatchExpectDTO dto);

    // quit match queue
    ResponseVO<Boolean> quit(Long userId);

    // get match queue info
    ResponseVO<QueueInfoVO> getQueueInfo();

    // match and put it to one room
    void match();
}
