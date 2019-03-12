package com.cross2u.ware.util;

import com.jfinal.plugin.druid.DruidPlugin;

public class MyDataSource {

    public static DruidPlugin druidPlugin;

    private static boolean debug = true;
    private static String jdbcUrl = "jdbc:mysql://120.79.79.141:3306/cross2u?characterEncoding=utf8&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPwd = "123456";

    public static javax.sql.DataSource getDataSource()
    {
        druidPlugin = new DruidPlugin(jdbcUrl, dbUser, dbPwd);
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }


}
