package cn.sbx0.microservices.bot.controller;


import cn.sbx0.microservices.bot.service.IWeixinService;
import cn.sbx0.microservices.bot.utils.AesException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wangh
 * @since 2022-08-23
 */
@RestController
@RequestMapping("/weixin")
public class WeixinController {
    @Resource
    private IWeixinService weixinService;

    @GetMapping("/auth")
    public String auth(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr
    ) throws AesException {
        return weixinService.auth(signature, timestamp, nonce, echostr);
    }

    @PostMapping("/auth")
    public String auth() {
        return "";
    }
}
