package cn.sbx0.microservices.account.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.impl.AccountServiceImpl;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.LoginDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangh
 * @since 2022-03-04
 */
@Api(value = "账户", tags = {"账户"})
@RestController
@RequestMapping("/")
public class AccountController extends BaseController<AccountServiceImpl, AccountMapper, AccountEntity> {

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public SaTokenInfo login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @ApiOperation(value = "注册")
    @PostMapping("/register")
    public Boolean register(@RequestBody LoginDTO dto) {
        return service.register(dto);
    }
}