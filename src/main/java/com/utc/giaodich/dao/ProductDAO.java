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

    // 1. Thêm sản phẩm mới
    public boolean addProduct(Product p, File selectedFile) {
        String sql = "INSERT INTO products (seller_id, name, price, quantity, description, image_path, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            File destDir = new File("uploads");
            if (!destDir.exists()) destDir.mkdir();
            File destFile = new File(destDir, fileName);
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            pstmt.setInt(1, p.getSellerId());
            pstmt.setString(2, p.getName());
            pstmt.setDouble(3, p.getPrice());
            pstmt.setInt(4, p.getQuantity());
            pstmt.setString(5, p.getDescription());
            pstmt.setString(6, "uploads/" + fileName);
            pstmt.setString(7, "AVAILABLE");
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 2. Lấy sản phẩm cho Người mua (Chỉ lấy món AVAILABLE)
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.username AS seller_username FROM products p " +
                "JOIN users u ON p.seller_id = u.id WHERE p.status = 'AVAILABLE'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getInt("seller_id"), rs.getString("name"),
                        rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description"),
                        rs.getString("image_path"), rs.getString("status"));
                product.setSellerUsername(rs.getString("seller_username"));
                products.add(product);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return products;
    }

    // 3. Lấy sản phẩm của Người bán (Ẩn những món đã DELETED)
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE seller_id = ? AND status != 'DELETED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt("id"), rs.getInt("seller_id"), rs.getString("name"),
                        rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description"),
                        rs.getString("image_path"), rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 4. XÓA MỀM (SOFT DELETE) - Đúng chuẩn thời thế, tránh lỗi khóa ngoại[cite: 6]
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE products SET status = 'DELETED' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 5. Lấy lịch sử mua hàng
    public List<Object[]> getPurchaseHistory(int buyerId) {
        List<Object[]> history = new ArrayList<>();
        String sql = "SELECT p.name, i.quantity, i.total_price, i.sale_date FROM invoices i " +
                "JOIN products p ON i.product_id = p.id WHERE i.buyer_id = ? ORDER BY i.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{rs.getString("name"), rs.getInt("quantity"),
                        rs.getDouble("total_price"), rs.getTimestamp("sale_date")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return history;
    }

    // 6. Lấy lịch sử bán hàng & Doanh thu
    public List<Object[]> getSalesHistory(int sellerId) {
        List<Object[]> history = new ArrayList<>();
        String sql = "SELECT u.username, i.quantity, i.total_price, i.sale_date FROM invoices i " +
                "JOIN products p ON i.product_id = p.id JOIN users u ON i.buyer_id = u.id " +
                "WHERE p.seller_id = ? ORDER BY i.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{rs.getString("username"), rs.getInt("quantity"),
                        rs.getDouble("total_price"), rs.getTimestamp("sale_date")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return history;
    }

    // 7. Cập nhật nhanh Giá/SL
    public boolean updateProduct(int productId, double newPrice, int newQuantity) {
        String sql = "UPDATE products SET price = ?, quantity = ?, status = IF(? > 0, 'AVAILABLE', 'SOLD') WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newQuantity);
            pstmt.setInt(3, newQuantity);
            pstmt.setInt(4, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}