package com.utc.giaodich.dao;

import com.utc.giaodich.config.DBConnection;
import com.utc.giaodich.model.Product;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Hàm thêm sản phẩm kèm xử lý file ảnh
    public boolean addProduct(Product p, File selectedFile) {
        String sql = "INSERT INTO products (seller_id, name, price, description, image_path) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Xử lý copy file ảnh vào thư mục dự án
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName(); // Đổi tên để không bị trùng
            File destDir = new File("uploads");
            if (!destDir.exists()) destDir.mkdir(); // Tạo thư mục nếu chưa có

            File destFile = new File(destDir, fileName);
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Lưu thông tin vào Database
            pstmt.setInt(1, p.getSellerId());
            pstmt.setString(2, p.getName());
            pstmt.setDouble(3, p.getPrice());
            pstmt.setString(4, p.getDescription());
            pstmt.setString(5, "uploads/" + fileName); // Lưu đường dẫn tương đối

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm lấy danh sách sản phẩm để Người mua xem
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE status = 'AVAILABLE'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getInt("seller_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}