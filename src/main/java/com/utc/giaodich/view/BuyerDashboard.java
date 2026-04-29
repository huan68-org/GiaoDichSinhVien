package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class BuyerDashboard extends JFrame {
    private User buyer;
    private JPanel pnlGrid; // Bảng chứa các sản phẩm

    public BuyerDashboard(User buyer) {
        this.buyer = buyer;
        setTitle("Sàn Đồ Cũ - Kênh Người Mua: " + buyer.getFullName());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel lblHeader = new JLabel("KHÁM PHÁ SẢN PHẨM MỚI NHẤT", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Khu vực chứa sản phẩm dạng lưới (3 cột, không giới hạn số hàng)
        pnlGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        pnlGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bọc pnlGrid vào thanh cuộn để vuốt được khi có nhiều đồ
        JScrollPane scrollPane = new JScrollPane(pnlGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Lăn chuột mượt hơn
        add(scrollPane, BorderLayout.CENTER);

        // Nạp dữ liệu
        loadProducts();
    }

    private void loadProducts() {
        ProductDAO dao = new ProductDAO();
        java.util.List<Product> products = dao.getAllProducts();

        java.text.DecimalFormat df = new java.text.DecimalFormat("#,### VNĐ");

        for (Product p : products) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            // Đổ bóng nhẹ bằng LineBorder màu xám nhạt
            card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            card.setBackground(Color.WHITE);

            // 1. Xử lý hiển thị Ảnh
            JLabel lblImage = new JLabel("", SwingConstants.CENTER);
            String projectPath = System.getProperty("user.dir");
            File imgFile = new File(projectPath, p.getImagePath());

            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image scaledImg = icon.getImage().getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaledImg));
            } else {
                lblImage.setText("Ảnh bị lỗi");
                lblImage.setForeground(Color.RED);
            }
            card.add(lblImage, BorderLayout.CENTER);

            // 2. Thông tin phía dưới (Tên, Giá, 2 Nút bấm)
            JPanel pnlInfo = new JPanel(new GridLayout(3, 1, 0, 5));
            pnlInfo.setBackground(Color.WHITE);
            pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding cho thoáng

            // Tên sản phẩm: Màu đen xám sang trọng
            JLabel lblName = new JLabel(p.getName(), SwingConstants.CENTER);
            lblName.setFont(new Font("Arial", Font.BOLD, 15));
            lblName.setForeground(new Color(50, 50, 50));

            // Giá tiền: Màu đỏ cam nổi bật
            JLabel lblPrice = new JLabel(df.format(p.getPrice()), SwingConstants.CENTER);
            lblPrice.setForeground(new Color(231, 76, 60));
            lblPrice.setFont(new Font("Arial", Font.BOLD, 16));

            // Khu vực chứa 2 nút: Chat và Mua Ngay
            JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 10, 0));
            pnlButtons.setBackground(Color.WHITE);

            // Nút Chat (Màu xanh dương đậm)
            JButton btnChat = new JButton("Chat");
            btnChat.setBackground(new Color(41, 128, 185));
            btnChat.setForeground(Color.WHITE);
            btnChat.setOpaque(true);
            btnChat.setBorderPainted(false);
            btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChat.addActionListener(e -> {
                new ChatFrame(buyer.getUsername(), "huan_seller").setVisible(true);
            });

            // Nút Mua Ngay (Màu cam Shopee)
            JButton btnBuy = new JButton("Mua Ngay");
            btnBuy.setBackground(new Color(230, 126, 34));
            btnBuy.setForeground(Color.WHITE);
            btnBuy.setOpaque(true);
            btnBuy.setBorderPainted(false);
            btnBuy.setFont(new Font("Arial", Font.BOLD, 13));
            btnBuy.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Sự kiện click nút Mua Ngay -> Mở giao diện Thanh toán
            btnBuy.addActionListener(e -> {
                new CheckoutFrame(buyer, p).setVisible(true);
            });

            pnlButtons.add(btnChat);
            pnlButtons.add(btnBuy);

            pnlInfo.add(lblName);
            pnlInfo.add(lblPrice);
            pnlInfo.add(pnlButtons);

            card.add(pnlInfo, BorderLayout.SOUTH);
            pnlGrid.add(card);
        }
    }
}