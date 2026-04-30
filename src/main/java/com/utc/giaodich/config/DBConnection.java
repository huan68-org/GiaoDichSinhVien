package com.utc.giaodich.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 1. URL kết nối: Đã thêm các tham số để fix lỗi Public Key và SSL trên Mac
    private static final String URL = "jdbc:mysql://localhost:3306/student_marketplace?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

    // 2. User mặc định của MySQL
    private static final String USER = "root";

    // 3. Mật khẩu 8 số bạn đã Initialize trong System Settings
    private static final String PASS = "12345678";

    /**
     * Phương thức lấy kết nối tới Database
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Đảm bảo Driver MySQL đã được nạp
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy thư viện MySQL JDBC Driver!");
            e.printStackTrace();
            throw new SQLException(e);
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối tới Database. Hãy kiểm tra MySQL đã Start chưa!");
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    // Hàm main để bạn test nhanh kết nối ngay tại đây
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Chúc mừng Huân! Kết nối Database thành công rực rỡ!");
            }
        } catch (SQLException e) {
            System.err.println("Kết nối thất bại! Hãy kiểm tra lại mật khẩu hoặc trạng thái MySQL.");
        }
    }
}