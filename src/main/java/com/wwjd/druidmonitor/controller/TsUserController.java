package com.wwjd.druidmonitor.controller;

import com.wwjd.druidmonitor.model.TsUser;
import com.wwjd.druidmonitor.service.ITsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制层
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @Created 2018年09月11日 15:01:00
 */
@RestController
public class TsUserController {

    @Autowired
    private ITsUserService tsUserService;

    @GetMapping("/finduser")
    public List<TsUser> findUser(){
        return tsUserService.findUser();
    }
}
