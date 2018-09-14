# 阿里 druid 数据源监控

***

### druid 数据源

###### 目前常用的数据源 c3p0、dbcp、proxool、druid，这里只说 druid.因为德鲁伊提供的这个监控组件，非常的便利，让我们直观感受 sql,调用的方法，还有访问 uri 等各方面的情况。

###### Druid 首先是一个数据库连接池，但它不仅仅是一个数据库连接池，它还包含一个 ProxyDriver，一系列内置的 JDBC 组件库，一个 SQLParser。Druid 支持所有 JDBC 兼容的数据库，包括 Oracle、MySql、Derby、Postgresql、SQLServer、H2 等等。 
###### Druid 针对 Oracle 和 MySql 做了特别优化，比如 Oracle 的 PSCache 内存占用优化，MySql 的 ping 检测优化。Druid 在监控、可扩展性、稳定性和性能方面都有明显的优势。Druid 提供了 Filter-Chain 模式的扩展 API，可以自己编写 Filter 拦截 JDBC 中的任何方法，可以在上面做任何事情，比如说性能监控、SQL 审计、用户名密码加密、日志等等。

###### 闲话不多说，现在进入主题，如何实现德鲁伊监控，下面阿导给出详细步骤，不足之处，多多指教

### 德鲁伊监控实现步骤

- 配置化方案设计

    1.  首先通过 spring 的 惯用伎俩 ConfigurationProperties 注解 DruidProperties 类去读取配置文件，该类包含两个内部类，其属性对应两个类。

    2. 因为我这边设计的是主从数据源，所以这边通过 Map<String,Druid> 对多个数据源进行相关配置，然后通过 Monitor 对监控的内容进行配置
    
    3. 这边给出详细代码，代码都加有注释，如下
    
```java

package com.wwjd.druidmonitor.druid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源属性
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @Created 2018年09月11日 09:24:00
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "spring.datasource")
public class DruidProperties {

    /**
     * 配置信息
     */
    private Map<String, Druid> druid = new LinkedHashMap<>();

    /**
     * 监控配置
     */
    private Monitor monitor;
    
    public Map<String, Druid> getDruid() {
        return druid;
    }
    
    public void setDruid(Map<String, Druid> druid) {
        this.druid = druid;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    /**
     * 读取配置
     *
     * @author 阿导
     * @CopyRight 万物皆导
     * @created 2018/9/11 9:32
     * @Modified_By 阿导 2018/9/11 9:32
     */

    public static class Druid{
        /**
         * 是否是主数据库
         */
        private boolean master = false;
        /**
         * 数据库连接地址
         */
        private String url;
        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * 驱动
         */
        private String driverClass = "com.mysql.cj.jdbc.Driver";

        /**
         * 初始化连接
         */
        private int initialSize = 1;
        /**
         * 最小空闲连接
         */
        private int minIdle = 1;
        /**
         *最大连接数
         */
        private int maxActive = 20;
        /**
         *最长等待时间
         */
        private int maxWait =30000;
        /**
         * 指明是否从池中取出连接前进行校验，校验失败则从池中去除并尝试获取另一个
         */
        private boolean testOnBorrow = true;
        /**
         * 指明是否归还到池中需要校验
         */
        private boolean testOnReturn = false;
        /**
         *  指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
         */
        private boolean testWhileIdle = false;
        /**
         * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
         */
        private int timeBetweenEvictionRunsMillis = 60000;
        /**
         *  配置一个连接在池中最小生存的时间，单位是毫秒
         */
        private int minEvictableIdleTimeMillis = 30000;

        public boolean isMaster() {
            return master;
        }

        public void setMaster(boolean master) {
            this.master = master;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public int getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(int initialSize) {
            this.initialSize = initialSize;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(int maxWait) {
            this.maxWait = maxWait;
        }

        public boolean isTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public boolean isTestOnReturn() {
            return testOnReturn;
        }

        public void setTestOnReturn(boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public boolean isTestWhileIdle() {
            return testWhileIdle;
        }

        public void setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }

        public int getTimeBetweenEvictionRunsMillis() {
            return timeBetweenEvictionRunsMillis;
        }

        public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }

        public int getMinEvictableIdleTimeMillis() {
            return minEvictableIdleTimeMillis;
        }

        public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        }
    }

    /**
     * 监控配置
     *
     * @author 阿导
     * @CopyRight 万物皆导
     * @created 2018/9/11 9:53
     * @Modified_By 阿导 2018/9/11 9:53
     */

    public static class Monitor{
        /**
         * 是否开启监控，默认关闭
         */
        private boolean monitor = false;
        /**
         *  SQL查询,用来验证从连接池取出的连接,在将连接返回给调用者之前.如果指定, 则查询必须是一个SQL SELECT并且必须返回至少一行记录
         */
        private String validationQuery = "SELECT 'x'";
        /**
         * 开启池的 prepared statement 池功能
         */
        private boolean poolPreparedStatements = true;
        /**
         * 不限制  statement池能够同时分配的打开的statements的最大数量,如果设置为0表示不限制
         */
        private int maxPoolPreparedStatementPerConnectionSize = 20;
        /**
         *  配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
         */
        private String filters = "stat,wall,log4j";
        /**
         *  通过connectProperties属性来打开mergeSql功能；慢SQL记录
         */
        private String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=1000;druid.stat.logSlowSql=true";
        /**
         * 合并多个DruidDataSource的监控数据
         */
        private boolean useGlobalDataSourceStat = true;

        /**
         * 扫描拦截的包，切点
         */
        private List<String> patterns;

        /**
         * 排除之外的路径
         */
        private List<String> excludedPatterns;

        /**
         * servlet 匹配地址
         */
        private String servlet = "/druid/*";
        /**
         * 账户
         */
        private String loginUsername = "dao";

        /**
         * 白名单
         */
        private List<String> allowList;
        /**
         * 黑名单
         */
        private List<String> denyList;

        /**
         * 拦截密码
         */
        private String loginPassword = "dao";
        /**
         * 是否能够重置数据
         */
        private String resetEnable = "false";
        /**
         * 登录拦截
         */
        private String loginFilter = "/*";
        /**
         * 允许通过的文件
         */
        private String loginPass="*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*";


        public boolean isMonitor() {
            return monitor;
        }

        public void setMonitor(boolean monitor) {
            this.monitor = monitor;
        }

        public String getValidationQuery() {
            return validationQuery;
        }

        public void setValidationQuery(String validationQuery) {
            this.validationQuery = validationQuery;
        }

        public boolean isPoolPreparedStatements() {
            return poolPreparedStatements;
        }

        public void setPoolPreparedStatements(boolean poolPreparedStatements) {
            this.poolPreparedStatements = poolPreparedStatements;
        }

        public int getMaxPoolPreparedStatementPerConnectionSize() {
            return maxPoolPreparedStatementPerConnectionSize;
        }

        public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
            this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
        }

        public String getFilters() {
            return filters;
        }

        public void setFilters(String filters) {
            this.filters = filters;
        }

        public String getConnectionProperties() {
            return connectionProperties;
        }

        public void setConnectionProperties(String connectionProperties) {
            this.connectionProperties = connectionProperties;
        }

        public boolean isUseGlobalDataSourceStat() {
            return useGlobalDataSourceStat;
        }

        public void setUseGlobalDataSourceStat(boolean useGlobalDataSourceStat) {
            this.useGlobalDataSourceStat = useGlobalDataSourceStat;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }

        public List<String> getExcludedPatterns() {
            return excludedPatterns;
        }

        public void setExcludedPatterns(List<String> excludedPatterns) {
            this.excludedPatterns = excludedPatterns;
        }

        public String getServlet() {
            return servlet;
        }

        public void setServlet(String servlet) {
            this.servlet = servlet;
        }

        public String getLoginUsername() {
            return loginUsername;
        }

        public void setLoginUsername(String loginUsername) {
            this.loginUsername = loginUsername;
        }

        public List<String> getAllowList() {
            return allowList;
        }

        public void setAllowList(List<String> allowList) {
            this.allowList = allowList;
        }

        public List<String> getDenyList() {
            return denyList;
        }

        public void setDenyList(List<String> denyList) {
            this.denyList = denyList;
        }

        public String getLoginPassword() {
            return loginPassword;
        }

        public void setLoginPassword(String loginPassword) {
            this.loginPassword = loginPassword;
        }

        public String getResetEnable() {
            return resetEnable;
        }

        public void setResetEnable(String resetEnable) {
            this.resetEnable = resetEnable;
        }

        public String getLoginFilter() {
            return loginFilter;
        }

        public void setLoginFilter(String loginFilter) {
            this.loginFilter = loginFilter;
        }

        public String getLoginPass() {
            return loginPass;
        }

        public void setLoginPass(String loginPass) {
            this.loginPass = loginPass;
        }
    }
}


```

- 数据源初始化

   1. 引入配置文件属性
    
   2. 创建数据源，并指明主从结构，并注册到 spring 容器中
    
   3. 话不多说，直接撸代码
   
```java

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


```   



- 德鲁伊监控额外信息配置

   1. 引入配置文件属性
   
   2. 配置需要扫描的包，通过切面编程，进行 spring 、 session 等监控
    
   3. 增加 Servlet 对访问资源进行限制
   
   4. 资源访问限制规则设置
   
   5. 老样子，代码给出
   
```java

package com.wwjd.druidmonitor.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 监控配置
 *
 * @author 阿导
 * @CopyRight 万物皆导
 * @Created 2018年09月11日 11:11:00
 */
public class MonitorConfiguration {

    /**
     * 配置自动注入
     */
    @Autowired
    private DruidProperties druidProperties;


    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    /**
     * 监控路径
     *
     * @author 阿导
     * @time 2018/9/11 11:40
     * @CopyRight 万物皆导
     * @param
     * @return
     */
    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut() throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        // 获取监控配置
        List<String> patterns = druidProperties.getMonitor().getPatterns();
        List<String> excludedPatterns = druidProperties.getMonitor().getExcludedPatterns();

        // 拦截配置
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        Class<?> clazz =druidStatPointcut.getClass();

        // 规则设置
        setPatterns(patterns,"patterns","initPatternRepresentation",clazz,druidStatPointcut);
        setPatterns(excludedPatterns,"excludedPatterns","initExcludedPatternRepresentation",clazz,druidStatPointcut);
        // 返回
        return druidStatPointcut;
    }

    /**
     * 设置拦截熟悉
     *
     * @author 阿导
     * @time 2018/9/11 13:26
     * @CopyRight 万物皆导
     * @param patterns
     * @param filedName
     * @param methodName
     * @param clazz
     * @param druidStatPointcut
     * @return
     */
    private void setPatterns(List<String> patterns, String filedName, String methodName, Class clazz, JdkRegexpMethodPointcut druidStatPointcut) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(CollectionUtils.isNotEmpty(patterns)){
            String[] array = new String[patterns.size()];
            patterns.toArray(array);
            Field field = clazz.getSuperclass().getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(druidStatPointcut,array);
            Method method = clazz.getDeclaredMethod(methodName, String[].class);
            method.setAccessible(true);
            method.invoke(druidStatPointcut,new Object[] {array});
        }
    }

    /**
     * druid
     *
     * @author 阿导
     * @time 2018/9/11 11:38
     * @CopyRight 万物皆导
     * @param
     * @return
     */
    @Bean
    public Advisor druidStatAdvisor() throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
    }


    /**
     * 声明一个 Servlet
     *
     * @author 阿导
     * @time 2018/9/11 11:36
     * @CopyRight 万物皆导
     * @param
     * @return
     */

    @Bean
    public ServletRegistrationBean druidStatViewServlet() {

        // 获取监控配置
        DruidProperties.Monitor monitor = druidProperties.getMonitor();

        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), monitor.getServlet());
        //添加初始化参数：initParams


        //白名单：
        if(CollectionUtils.isNotEmpty(monitor.getAllowList())) {
            monitor.getAllowList().stream().forEach(allow->  servletRegistrationBean.addInitParameter("allow", allow));
        }

        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        if(CollectionUtils.isNotEmpty(monitor.getDenyList())) {
            monitor.getDenyList().stream().forEach(deny->  servletRegistrationBean.addInitParameter("deny", deny));
        }

        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", monitor.getLoginUsername());
        servletRegistrationBean.addInitParameter("loginPassword", monitor.getLoginPassword());

        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable", monitor.getResetEnable());

        return servletRegistrationBean;
    }


    /**
     *  注册一个：filterRegistrationBean
     *
     * @author 阿导
     * @time 2018/9/11 11:37
     * @CopyRight 万物皆导
     * @param
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        // 获取监控配置
        DruidProperties.Monitor monitor = druidProperties.getMonitor();

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns(monitor.getLoginFilter());
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", monitor.getLoginPass());
        return filterRegistrationBean;

    }
}


```   

- 加个注解，让德鲁伊监控除 sql 之外的配置可用，因为有些项目没必要加监控

    没啥好说的，直接给代码
    
```java

    package com.wwjd.druidmonitor.druid;
    
    import org.springframework.context.annotation.Import;
    
    import java.lang.annotation.*;
    
    /**
     * 开启监控
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
    
    
```    

- 所有能配置的参数列举

```properties

    # 是否是主数据库
    spring.datasource.druid.${instance}.master=true
    # 数据连接地址
    spring.datasource.druid.${instance}.url=jdbc:mysql://127.0.0.1:3306/dao?useSSL=false&useUnicode=true&characterEncoding=utf-8
    # 数据用户名
    spring.datasource.druid.${instance}.username=root
    # 数据库密码
    spring.datasource.druid.${instance}.password=123456
    # 数据库驱动
    spring.datasource.druid.${instance}.driver-class=com.mysql.cj.jdbc.Driver
    # 初始化连接
    spring.datasource.druid.${instance}.initial-size=1
    # 最小空闲连接
    spring.datasource.druid.${instance}.min-idle=1
    # 最大连接数
    spring.datasource.druid.${instance}.max-active=20
    # 最长等待时间
    spring.datasource.druid.${instance}.max-wait=30000
    # 指明是否从池中取出连接前进行校验，校验失败则从池中去除并尝试获取另一个
    spring.datasource.druid.${instance}.test-on-borrow=true
    # 指明是否归还到池中需要校验
    spring.datasource.druid.${instance}.test-on-return=false
    # 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
    spring.datasource.druid.${instance}.test-while-idle=false
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    spring.datasource.druid.${instance}.time-between-eviction-runs-millis=60000
    # 配置一个连接在池中最小生存的时间，单位是毫秒
    spring.datasource.druid.${instance}.min-evictable-idle-time-millis= 30000
    
    
    # 开启 druid 监控
    spring.datasource.monitor.monitor=true
    #  SQL查询,用来验证从连接池取出的连接,在将连接返回给调用者之前.如果指定, 则查询必须是一个SQL SELECT并且必须返回至少一行记录
    spring.datasource.monitor.validation-query=SELECT 'x'
    # 开启池的 prepared statement 池功能
    spring.datasource.monitor.pool-prepared-statements=true
    # 不限制  statement池能够同时分配的打开的statements的最大数量,如果设置为0表示不限制
    spring.datasource.monitor.max-pool-prepared-statement-per-connection-size=20
    #  配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    spring.datasource.monitor.filters=stat,wall,log4j
    # 通过 connectProperties 属性来打开 mergeSql 功能；慢 SQL 记录
    spring.datasource.monitor.connection-properties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=1000;druid.stat.logSlowSql=true
    # 合并多个DruidDataSource的监控数据
    spring.datasource.monitor.use-global-data-source-stat=true
    #  servlet 匹配地址
    spring.datasource.monitor.servlet=/druid/*
    # 登录拦截
    spring.datasource.monitor.login-username=dao
    # 登录密码
    spring.datasource.monitor.login-password=dao
    # 访问白名单
    spring.datasource.monitor.allow-list=127.0.0.4,127.0.0.5
    # 访问黑名单
    spring.datasource.monitor.deny-list=127.0.0.1,127.0.0.2
    # 是否能够重置数据
    spring.datasource.monitor.reset-enable=false
    # 登录拦截
    spring.datasource.monitor.login-filter=/*
    # 允许通过的文件
    spring.datasource.monitor.login-pass=*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*
    # spring 监控拦截的包，多个以半角逗号分隔
    spring.datasource.monitor.patterns=com.wwjd.druidmonitor.mapper.*,com.wwjd.druidmonitor.service.impl.*
    # 不监控的包
    spring.datasource.monitor.excluded-patterns=com.wwjd.druidmonitor.controller.*
            
    
```
- 给出成功案例地址,[点击前往](https://github.com/wanwujiedao/druid-monitor)


- 最后访问监控地址，http:// 项目IP:项目端口/druid/


