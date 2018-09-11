package com.wwjd.druidmonitor;

import com.wwjd.druidmonitor.druid.EnableMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *  启动类
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @created 2018/9/11 15:05
 * @Modified_By 阿导 2018/9/11 15:05
 */

@SpringBootApplication
@EnableConfigurationProperties
@EnableMonitor
@MapperScan(basePackages = "com.wwjd.druidmonitor.mapper")
public class DruidMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DruidMonitorApplication.class, args);
    }
}
