package com.utc.giaodich.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Kết nối tới DB student_marketplace đã tạo trên phpMyAdmin
    private static final String URL = "jdbc:mysql://localhost:3306/student_marketplace?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = ""; // XAMPP mặc định pass rỗng

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL Driver. Kiểm tra lại pom.xml!");
        }
    }
}