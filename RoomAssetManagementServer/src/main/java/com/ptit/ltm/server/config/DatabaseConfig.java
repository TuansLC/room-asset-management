package com.ptit.ltm.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/room_asset_db";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";
    
    private static DatabaseConfig instance;
    
    private DatabaseConfig() {}
    
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            log.info("Đã kết nối thành công đến cơ sở dữ liệu");
            return conn;
        } catch (SQLException e) {
            log.error("Lỗi kết nối cơ sở dữ liệu: {}", e.getMessage());
            throw e;
        }
    }
}