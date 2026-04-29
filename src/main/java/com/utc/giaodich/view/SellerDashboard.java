package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SellerDashboard extends JFrame {
    private User seller; // Lưu thông tin người bán đang đăng nhập
    private File selectedFile; // Lưu trữ file ảnh người dùng vừa chọn

    // Các thành phần giao diện
    private JTextField txtName, txtPrice;
    private JTextArea txtDescription;
    private JLabel lblImagePreview;
    private JButton btnChooseImage, btnSubmit, btnInbox;

    // Constructor nhận vào đối tượng User để biết ai đang đăng bán
    public SellerDashboard(User seller) {
        this.seller = seller;
        setTitle("Sàn Đồ Cũ - Kênh Người Bán: " + seller.getFullName());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- CỘT TRÁI: NHẬP THÔNG TIN ---
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        pnlLeft.setPreferredSize(new Dimension(400, 500));

        JPanel pnlInput = new JPanel(new GridLayout(6, 1, 5, 5));
        pnlInput.add(new JLabel("Tên sản phẩm:"));
        txtName = new JTextField(); pnlInput.add(txtName);

        pnlInput.add(new JLabel("Giá bán (VNĐ):"));
        txtPrice = new JTextField(); pnlInput.add(txtPrice);

        pnlInput.add(new JLabel("Mô tả chi tiết:"));
        txtDescription = new JTextArea(3, 20);
        txtDescription.setLineWrap(true);
        pnlInput.add(new JScrollPane(txtDescription));

        pnlLeft.add(pnlInput, BorderLayout.NORTH);

        // --- CỘT PHẢI: KHU VỰC ẢNH ---
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Hình ảnh sản phẩm"));

        lblImagePreview = new JLabel("Chưa chọn ảnh", SwingConstants.CENTER);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnlRight.add(lblImagePreview, BorderLayout.CENTER);

        JPanel pnlImageBtns = new JPanel();
        btnChooseImage = new JButton("Chọn ảnh từ máy...");
        pnlImageBtns.add(btnChooseImage);
        pnlRight.add(pnlImageBtns, BorderLayout.SOUTH);

        // --- KHU VỰC DƯỚI CÙNG: HỘP THƯ VÀ NÚT ĐĂNG BÁN ---
        JPanel pnlBottom = new JPanel(); // Khởi tạo Panel trước tiên

        // Thêm nút Hộp thư cho Người Bán
        btnInbox = new JButton("HỘP THƯ (MỞ ĐỂ NHẬN TIN)");
        btnInbox.setBackground(new Color(52, 152, 219));
        btnInbox.setForeground(Color.WHITE);
        btnInbox.setOpaque(true);
        btnInbox.setBorderPainted(false);
        pnlBottom.add(btnInbox); // Lắp vào Panel

        // Thêm Nút đăng bán
        btnSubmit = new JButton("ĐĂNG BÁN SẢN PHẨM MỚI");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(46, 204, 113)); // Màu xanh lá cây
        btnSubmit.setOpaque(true);
        pnlBottom.add(btnSubmit); // Lắp vào Panel

        // Add 3 khu vực vào JFrame
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---
        btnChooseImage.addActionListener(e -> chooseImage());

        btnSubmit.addActionListener(e -> submitProduct());

        // Sự kiện mở Chat cho Người Bán
        btnInbox.addActionListener(e -> {
            new ChatFrame(seller.getUsername(), "ha_buyer").setVisible(true);
        });
    }

    // Hàm xử lý mở hộp thoại chọn file trên Mac
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh sản phẩm");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(scaledImage));
            lblImagePreview.setText("");
        }
    }

    // Hàm xử lý Đăng bán (Kết nối với DAO)
    private void submitProduct() {
        if (txtName.getText().isEmpty() || txtPrice.getText().isEmpty() || selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin và chọn ảnh!");
            return;
        }

        try {
            double price = Double.parseDouble(txtPrice.getText());

            // 1. Tạo đối tượng Product
            Product p = new Product(0, seller.getId(), txtName.getText(), price, txtDescription.getText(), "", "AVAILABLE");

            // 2. Gọi DAO để lưu vào DB và copy file ảnh
            ProductDAO dao = new ProductDAO();
            if (dao.addProduct(p, selectedFile)) {
                JOptionPane.showMessageDialog(this, "Đăng bán thành công!");
                txtName.setText(""); txtPrice.setText(""); txtDescription.setText("");
                lblImagePreview.setIcon(null); lblImagePreview.setText("Chưa chọn ảnh");
                selectedFile = null;
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu dữ liệu.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá tiền phải là số!");
        }
    }
}