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
    private JPanel pnlGrid; // Panel chứa lưới sản phẩm
    private Timer autoRefreshTimer; // Bộ đếm giờ tự động cập nhật

    public BuyerDashboard(User buyer) {
        this.buyer = buyer;
        setTitle("Sàn Giao dịch sinh viên - Kênh Người Mua: " + buyer.getFullName());
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Tắt app khi đóng form
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("KHÁM PHÁ SẢN PHẨM MỚI NHẤT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pnlHeader.add(lblTitle, BorderLayout.CENTER);

        // Thêm nút "Làm mới" thủ công cho người dùng thiếu kiên nhẫn
        JButton btnRefresh = new JButton("Làm mới ↺");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadProducts()); // Bấm là tải lại
        pnlHeader.add(btnRefresh, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- KHU VỰC SẢN PHẨM ---
        // Lưới hiển thị sản phẩm (4 cột, số hàng tự giãn)
        pnlGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        pnlGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(pnlGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Lăn chuột mượt hơn
        add(scrollPane, BorderLayout.CENTER);

        // --- NẠP DỮ LIỆU ---
        loadProducts(); // Nạp lần đầu tiên khi vừa mở app

        // --- TÍNH NĂNG AUTO-REFRESH (REAL-TIME FAKE) ---
        // Cứ mỗi 5000 mili-giây (5 giây) là tự động gọi lại hàm loadProducts()
        autoRefreshTimer = new Timer(5000, e -> loadProducts());
        autoRefreshTimer.start();
    }

    // Hàm đảm nhiệm việc lấy dữ liệu từ Database và vẽ lên màn hình
    private void loadProducts() {
        pnlGrid.removeAll(); // Xóa sạch các thẻ sản phẩm cũ trên màn hình

        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getAllProducts(); // Lấy danh sách mới nhất từ DB
        DecimalFormat df = new DecimalFormat("#,###.## VNĐ");

        // Duyệt qua từng sản phẩm và tạo "Card" hiển thị
        for (Product p : products) {
            JPanel pnlCard = new JPanel(new BorderLayout(5, 5));
            pnlCard.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            pnlCard.setBackground(Color.WHITE);

            // 1. Ảnh sản phẩm
            JLabel lblImage = new JLabel("", SwingConstants.CENTER);
            lblImage.setPreferredSize(new Dimension(200, 180));
            try {
                // Tải ảnh từ thư mục uploads (nếu có lỗi sẽ bỏ qua để không sập app)
                ImageIcon icon = new ImageIcon(p.getImagePath());
                Image scaledImg = icon.getImage().getScaledInstance(200, 180, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                lblImage.setText("No Image");
            }
            pnlCard.add(lblImage, BorderLayout.NORTH);

            // 2. Thông tin (Tên + Giá)
            JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
            pnlInfo.setBackground(Color.WHITE);
            JLabel lblName = new JLabel(p.getName(), SwingConstants.CENTER);
            lblName.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel lblPrice = new JLabel(df.format(p.getPrice()), SwingConstants.CENTER);
            lblPrice.setForeground(new Color(231, 76, 60)); // Màu đỏ tươi
            lblPrice.setFont(new Font("Arial", Font.BOLD, 14));

            pnlInfo.add(lblName);
            pnlInfo.add(lblPrice);
            pnlCard.add(pnlInfo, BorderLayout.CENTER);

            // 3. Nút bấm (Chat + Mua Ngay)
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

            // Gắn sự kiện cho nút
            btnBuy.addActionListener(e -> {
                // Tạm dừng timer khi đang mở form thanh toán để tránh giật lag
                autoRefreshTimer.stop();

                CheckoutFrame checkout = new CheckoutFrame(buyer, p);
                checkout.setVisible(true);

                // Khi đóng form thanh toán, bật lại timer
                checkout.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        autoRefreshTimer.start();
                        loadProducts(); // Load lại ngay lập tức để cập nhật số lượng
                    }
                });
            });

            btnChat.addActionListener(e -> new ChatFrame(buyer.getUsername(), "seller_" + p.getSellerId()).setVisible(true));

            pnlButtons.add(btnChat);
            pnlButtons.add(btnBuy);
            pnlCard.add(pnlButtons, BorderLayout.SOUTH);

            pnlGrid.add(pnlCard); // Thêm thẻ vừa tạo vào lưới
        }

        // 3 lệnh bắt buộc của Java Swing để vẽ lại giao diện sau khi thay đổi thành phần
        pnlGrid.revalidate();
        pnlGrid.repaint();
    }
}