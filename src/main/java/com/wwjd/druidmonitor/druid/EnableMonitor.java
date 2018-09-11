package com.wwjd.druidmonitor.druid;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启 监控
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @created 2018/8/20 15:42
 * @Modified_By 阿导 2018/8/20 15:42
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ MonitorConfiguration.class})
public @interface EnableMonitor {
}