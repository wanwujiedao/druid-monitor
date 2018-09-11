package com.wwjd.druidmonitor.service.impl;

import com.wwjd.druidmonitor.mapper.TsUserMapper;
import com.wwjd.druidmonitor.model.TsUser;
import com.wwjd.druidmonitor.service.ITsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务层
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @Created 2018年09月11日 14:58:00
 */
@Service
public class TsUserServiceImpl implements ITsUserService {

    @Autowired
    private TsUserMapper tsUserMapper;
    @Override
    public List<TsUser> findUser() {
        return tsUserMapper.selectAll();
    }
}
