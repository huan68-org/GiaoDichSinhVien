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

    // 1. Hàm thêm sản phẩm mới (Đã tích hợp Số lượng)
    public boolean addProduct(Product p, File selectedFile) {
        String sql = "INSERT INTO products (seller_id, name, price, quantity, description, image_path, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Xử lý copy file ảnh vào thư mục dự án
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            File destDir = new File("uploads");
            if (!destDir.exists()) destDir.mkdir(); // Tạo thư mục nếu chưa có

            File destFile = new File(destDir, fileName);
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Lưu thông tin vào Database
            pstmt.setInt(1, p.getSellerId());
            pstmt.setString(2, p.getName());
            pstmt.setDouble(3, p.getPrice());
            pstmt.setInt(4, p.getQuantity()); // LƯU SỐ LƯỢNG MỚI THÊM
            pstmt.setString(5, p.getDescription());
            pstmt.setString(6, "uploads/" + fileName); // Lưu đường dẫn tương đối
            pstmt.setString(7, "AVAILABLE");

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Hàm lấy danh sách sản phẩm để Người mua xem (BuyerDashboard)
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
                        rs.getInt("quantity"), // CẬP NHẬT LẤY SỐ LƯỢNG
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

    // 3. Hàm lấy danh sách sản phẩm của MỘT người bán (Cho giao diện Quản lý)
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE seller_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getInt("seller_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"), // CẬP NHẬT LẤY SỐ LƯỢNG
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

    // 4. Hàm xóa sản phẩm
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Hàm cập nhật nhanh Giá và Số lượng (Khi người bán muốn sửa)
    public boolean updateProduct(int productId, double newPrice, int newQuantity) {
        // Cập nhật giá, số lượng. Đồng thời nếu số lượng > 0 thì tự gán lại thành AVAILABLE, nếu bằng 0 thì SOLD
        String sql = "UPDATE products SET price = ?, quantity = ?, status = IF(? > 0, 'AVAILABLE', 'SOLD') WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newQuantity);
            pstmt.setInt(3, newQuantity); // Dùng cho câu lệnh IF check > 0 ở trên
            pstmt.setInt(4, productId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}