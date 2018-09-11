package com.wwjd.druidmonitor.druid;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.config.MasterSlaveRuleConfiguration;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * druid 数据源
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @created 2018/8/16 10:21
 * @Modified_By 阿导 2018/8/16 10:21
 */
@Configuration
@EnableConfigurationProperties({DruidProperties.class})
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DruidAutoConfiguration {

    /**
     * 配置自动注入
     */
    @Autowired
    private DruidProperties druidProperties;
    @Bean
    @Primary
    public DataSource dataSource() {
        // 校验非空
        Objects.requireNonNull(druidProperties,"数据库 连接信息不能为空");
        // 获取监控
        DruidProperties.Monitor monitor = druidProperties.getMonitor();
        // 获取数据源
        Map<String,DruidProperties.Druid> druidMap = druidProperties.getDruid();
        Map<String, DataSource> dataSourceMap = new HashMap<>(druidMap.size());
        // 村塾数据源
        List<String> druidKey = new ArrayList<>();
        AtomicReference<Boolean> isFirst = new AtomicReference<>(true);
        // 是否有设置主数据库
       AtomicReference<String> sbMaster = new AtomicReference<>(new String());

        druidMap.forEach((key,config)->{
            // 数据源配置
            dataSourceMap.put(key,createDataSource(config,monitor));
            druidKey.add(key);
            if( isFirst.get()){
                sbMaster.set(key);
                isFirst.set(false);
            }else if(config.isMaster() && StringUtil.isEmpty(sbMaster.get())){
                sbMaster.set(key);
                isFirst.set(false);
            }
        });

        // 主从角色配置
        MasterSlaveRuleConfiguration dataRoleConfig = this.dataRoleConfig(druidKey,sbMaster.get());
        Map<String, Object> configMap = new HashMap<>(druidKey.size());
        DataSource dataSource;
        try {
            dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, dataRoleConfig, configMap);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }


    /**
     * 主从配置，若没有主数据库，取第一个，若多个主数据库，取扫描到的第一个
     *
     * @author 阿导
     * @time 2018/9/11 10:36
     * @CopyRight 万物皆导
     * @param druidKey
     * @return
     */
    private MasterSlaveRuleConfiguration dataRoleConfig(List<String> druidKey ,String sbMaster) {
        MasterSlaveRuleConfiguration dataRoleConfig = new MasterSlaveRuleConfiguration();
        // 设置主从
        dataRoleConfig.setName("master_slave");
        // 遍历设置数据源
       druidKey.remove(sbMaster);
       // 主数据库
       dataRoleConfig.setMasterDataSourceName(sbMaster);

       // 从数据库
       if(CollectionUtils.isNotEmpty(druidKey)){
           dataRoleConfig.setSlaveDataSourceNames(druidKey);
       }

       // 返回角色配置
        return dataRoleConfig;
    }

    /**
     * 创建数据源
     *
     * @author 阿导
     * @time 2018/9/11 11:07
     * @CopyRight 万物皆导
     * @param config
     * @param monitor
     * @return
     */
    private DruidDataSource createDataSource(DruidProperties.Druid config,DruidProperties.Monitor monitor) {

        // 创建数据源
        DruidDataSource dataSource = new DruidDataSource();
        // 数据库连接地址
        dataSource.setUrl(config.getUrl());
        // 数据库用户名
        dataSource.setUsername(config.getUsername());
        // 数据库密码
        dataSource.setPassword(config.getPassword());
        // 下面就不说了和属性那边注释对照一下
        if (config.getInitialSize() > 0) {
            dataSource.setInitialSize(config.getInitialSize());
        }
        if (config.getMinIdle() > 0) {
            dataSource.setMinIdle(config.getMinIdle());
        }
        if (config.getMaxActive() > 0) {
            dataSource.setMaxActive(config.getMaxActive());
        }
        if (config.getMaxWait() > 0) {
            dataSource.setMaxWait(config.getMaxWait());
        }
        dataSource.setTestOnBorrow(config.isTestOnBorrow());
        dataSource.setTestOnReturn(config.isTestOnReturn());
        dataSource.setTestWhileIdle(config.isTestWhileIdle());
        dataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());


        // 监控资源处理
        if(monitor.isMonitor()) {
            dataSource.setValidationQuery(monitor.getValidationQuery());
            dataSource.setPoolPreparedStatements(monitor.isPoolPreparedStatements());
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(monitor.getMaxPoolPreparedStatementPerConnectionSize());
            dataSource.setUseGlobalDataSourceStat(monitor.isUseGlobalDataSourceStat());
            try {
                dataSource.setFilters(monitor.getFilters());
            } catch (SQLException e) {
                System.err.println("druid configuration initialization filter: " + e);
            }
            dataSource.setConnectionProperties(monitor.getConnectionProperties());
            try {
                dataSource.init();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return dataSource;

    }

}
