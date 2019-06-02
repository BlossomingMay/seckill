package com.huang.springboot.controller;

import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.domain.User;
import com.huang.springboot.result.Result;
import com.huang.springboot.service.FlashSaleUserService;
import com.huang.springboot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    FlashSaleUserService userService;

    @RequestMapping("info")
    @ResponseBody
    public Result<FlashSaleUser> getUser(Model model, FlashSaleUser flashSaleUser){
        return Result.success(flashSaleUser);

    }
}
