package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.service.IMessageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/5/11
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private IMessageService messageService;

    @GetMapping(value = "/subscribe/{code}", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable("code") String code) {
        return messageService.subscribe(code);
    }
}
