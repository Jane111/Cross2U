package com.cross2u.anaylsis.util;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConfigurationProperties(prefix = "jfinal")
public class JfinalOrmConfig {

    /**
     * 创建数据库连接池
     */
    public static DruidPlugin druidPlugin;
//    @Bean
//    public boolean createDatabaseConnectionPool() {
//        druidPlugin = new DruidPlugin(jdbcUrl, dbUser, dbPwd);
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//        arp.setDialect(new MysqlDialect());
//        arp.setShowSql(debug);
//        _MappingKit.mapping(arp);
//        druidPlugin.start();
//        return arp.start();
//    }


    public static DataSource getDataSource()
    {
        druidPlugin = new DruidPlugin(jdbcUrl, dbUser, dbPwd);
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    private static boolean debug = true;
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/cross2u?characterEncoding=utf8&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPwd = "password";

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }
}
