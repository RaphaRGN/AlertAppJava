package com.raphaelprojetos.sentinel.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:postgresql://192.168.0.110/Sentinel");
        config.setUsername("postgres");
        config.setPassword("root");
        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource(config);

    }


    private Database(){}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

