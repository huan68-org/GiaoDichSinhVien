package com.utc.giaodich.dao;

import com.utc.giaodich.config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // 1. Gửi tin nhắn
    public boolean sendMessage(String sender, String receiver, String content) {
        String sql = "INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, content);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Lấy toàn bộ lịch sử chat giữa 2 người (Sắp xếp từ cũ đến mới)
    public String getChatHistory(String user1, String user2) {
        StringBuilder history = new StringBuilder();
        String sql = "SELECT sender, content, DATE_FORMAT(created_at, '%H:%i') as time " +
                "FROM messages " +
                "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                "ORDER BY created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                String time = rs.getString("time");

                // Trình bày tin nhắn: [14:30] huan: Chào shop!
                history.append("[").append(time).append("] ")
                        .append(sender).append(": ")
                        .append(content).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }
}