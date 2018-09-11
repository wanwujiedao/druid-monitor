package com.wwjd.druidmonitor.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用 mapper
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @created 2018/8/7 13:05
 * @Modified_By 阿导 2018/8/7 13:05
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}