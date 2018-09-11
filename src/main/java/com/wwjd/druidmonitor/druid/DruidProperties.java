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
         *  通过 connectProperties 属性来打开 mergeSql 功能；慢 SQL 记录
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
        private String loginPass="*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";


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
