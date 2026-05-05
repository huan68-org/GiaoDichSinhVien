package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class BuyerDashboard extends JFrame {
    private User buyer;
    private JPanel pnlGrid;
    private Timer autoRefreshTimer;

    public BuyerDashboard(User buyer) {
        this.buyer = buyer;
        setTitle("Sàn Giao dịch sinh viên - Kênh Người Mua: " + buyer.getFullName());
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("KHÁM PHÁ SẢN PHẨM MỚI NHẤT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pnlHeader.add(lblTitle, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Làm mới ↺");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadProducts());
        pnlHeader.add(btnRefresh, BorderLayout.EAST);

        // Tìm đến pnlHeader và thêm đoạn này vào
        JButton btnHistory = new JButton("Lịch sử mua 📋");
        btnHistory.addActionListener(e -> new HistoryFrame("Lịch sử mua hàng của bạn", buyer.getId(), false).setVisible(true));
        pnlHeader.add(btnHistory, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- KHU VỰC SẢN PHẨM ---
        pnlGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        pnlGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(pnlGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- NẠP DỮ LIỆU ---
        loadProducts();

        // --- AUTO-REFRESH (5 GIÂY) ---
        autoRefreshTimer = new Timer(5000, e -> loadProducts());
        autoRefreshTimer.start();
    }

    private void loadProducts() {
        pnlGrid.removeAll();

        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getAllProducts();
        DecimalFormat df = new DecimalFormat("#,###.## VNĐ");

        for (Product p : products) {
            JPanel pnlCard = new JPanel(new BorderLayout(5, 5));
            pnlCard.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            pnlCard.setBackground(Color.WHITE);

            // 1. Ảnh sản phẩm
            JLabel lblImage = new JLabel("", SwingConstants.CENTER);
            lblImage.setPreferredSize(new Dimension(200, 180));
            try {
                ImageIcon icon = new ImageIcon(p.getImagePath());
                Image scaledImg = icon.getImage().getScaledInstance(200, 180, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                lblImage.setText("No Image");
            }
            pnlCard.add(lblImage, BorderLayout.NORTH);

            // 2. Thông tin (Tên + Giá + Số lượng)
            JPanel pnlInfo = new JPanel(new GridLayout(3, 1)); // Tăng lên 3 hàng để chứa dòng số lượng
            pnlInfo.setBackground(Color.WHITE);

            JLabel lblName = new JLabel(p.getName(), SwingConstants.CENTER);
            lblName.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel lblPrice = new JLabel(df.format(p.getPrice()), SwingConstants.CENTER);
            lblPrice.setForeground(new Color(231, 76, 60));
            lblPrice.setFont(new Font("Arial", Font.BOLD, 14));

            // THÊM: Hiển thị số lượng còn lại
            JLabel lblStock = new JLabel("Còn lại: " + p.getQuantity(), SwingConstants.CENTER);
            lblStock.setFont(new Font("Arial", Font.ITALIC, 12));
            lblStock.setForeground(new Color(41, 128, 185));

            pnlInfo.add(lblName);
            pnlInfo.add(lblPrice);
            pnlInfo.add(lblStock);
            pnlCard.add(pnlInfo, BorderLayout.CENTER);

            // 3. Nút bấm
            JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 5, 5));
            pnlButtons.setBackground(Color.WHITE);

            JButton btnChat = new JButton("Chat");
            btnChat.setBackground(new Color(52, 152, 219));
            btnChat.setForeground(Color.WHITE);
            btnChat.setOpaque(true); btnChat.setBorderPainted(false);

            JButton btnBuy = new JButton("Mua Ngay");
            btnBuy.setBackground(new Color(230, 126, 34));
            btnBuy.setForeground(Color.WHITE);
            btnBuy.setOpaque(true); btnBuy.setBorderPainted(false);

            btnBuy.addActionListener(e -> {
                autoRefreshTimer.stop();
                CheckoutFrame checkout = new CheckoutFrame(buyer, p);
                checkout.setVisible(true);
                checkout.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        autoRefreshTimer.start();
                        loadProducts();
                    }
                });
            });

            // CHỈNH SỬA: Lấy chính xác username của người bán để mở Chat
            btnChat.addActionListener(e -> {
                String targetSeller = p.getSellerUsername() != null ? p.getSellerUsername() : "seller_" + p.getSellerId();
                new ChatFrame(buyer.getUsername(), targetSeller).setVisible(true);
            });

            pnlButtons.add(btnChat);
            pnlButtons.add(btnBuy);
            pnlCard.add(pnlButtons, BorderLayout.SOUTH);

            pnlGrid.add(pnlCard);
        }

        pnlGrid.revalidate();
        pnlGrid.repaint();
    }
}