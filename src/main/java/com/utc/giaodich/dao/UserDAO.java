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
}