package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.service.IMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@Slf4j
@Service
public class MatchServiceImpl implements IMatchService {
    private final Deque<MatchExpectDTO> QUEUE = new ConcurrentLinkedDeque<>();

    @Override
    public ResponseVO<Boolean> join(MatchExpectDTO dto) {
        QUEUE.addFirst(dto);
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<Boolean> quit(Long userId) {
        AtomicBoolean find = new AtomicBoolean(false);
        QUEUE.forEach(q -> {
            if (userId.equals(q.getUserId())) {
                QUEUE.remove(q);
                find.set(true);
            }
        });
        return new ResponseVO<>(find.get() ? ResponseVO.SUCCESS : ResponseVO.FAILED, find.get());
    }

    @Override
    public ResponseVO<Integer> getQueueInfo() {
        return new ResponseVO<>(ResponseVO.SUCCESS, QUEUE.size());
    }
}
