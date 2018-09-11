package com.wwjd.druidmonitor.service;

import com.wwjd.druidmonitor.model.TsUser;

import java.util.List;

/**
 * 用户业务层
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @Created 2018年09月11日 14:57:00
 */
public interface ITsUserService {

    /**
     * 查询用户
     *
     * @author 阿导
     * @time 2018/9/11 15:03
     * @CopyRight 万物皆导
     * @param
     * @return
     */
    List<TsUser> findUser();

}
