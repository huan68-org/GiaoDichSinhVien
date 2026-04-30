package com.utc.giaodich.dao;

import com.utc.giaodich.config.DBConnection;
import com.utc.giaodich.model.User;
import java.sql.*;

public class UserDAO {
    // Hàm này trả về đối tượng User nếu đúng, trả về null nếu sai pass
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role") // Lấy vai trò BUYER/SELLER để lát nữa phân luồng
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm đăng ký tài khoản mới
    public boolean register(User user) {
        // Câu lệnh SQL thêm user mới vào CSDL
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());

            // executeUpdate trả về số dòng bị ảnh hưởng. Nếu > 0 nghĩa là insert thành công
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}