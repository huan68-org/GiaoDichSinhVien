package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SellerDashboard extends JFrame {
    private User seller;
    private File selectedFile;

    // Các thành phần giao diện
    private JTextField txtName, txtPrice, txtQuantity;
    private JTextArea txtDescription;
    private JLabel lblImagePreview;
    private JButton btnChooseImage, btnSubmit;

    public SellerDashboard(User seller) {
        this.seller = seller;
        setTitle("Đăng Sản Phẩm Mới");
        setSize(800, 500);
        // Quan trọng: Chỉ tắt form này, không tắt cả chương trình
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- CỘT TRÁI: NHẬP THÔNG TIN ---
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        pnlLeft.setPreferredSize(new Dimension(400, 500));

        // Sử dụng BoxLayout để các ô nhập liệu không bị chia đều chiều cao một cách cứng nhắc
        JPanel pnlInput = new JPanel();
        pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.Y_AXIS));

        pnlInput.add(new JLabel("Tên sản phẩm:"));
        txtName = new JTextField();
        txtName.setMaximumSize(new Dimension(400, 30));
        pnlInput.add(txtName);

        pnlInput.add(Box.createVerticalStrut(10));
        pnlInput.add(new JLabel("Giá bán (VNĐ):"));
        txtPrice = new JTextField();
        txtPrice.setMaximumSize(new Dimension(400, 30));
        pnlInput.add(txtPrice);

        pnlInput.add(Box.createVerticalStrut(10));
        pnlInput.add(new JLabel("Số lượng trong kho:"));
        txtQuantity = new JTextField("1");
        txtQuantity.setMaximumSize(new Dimension(400, 30));
        pnlInput.add(txtQuantity);

        pnlInput.add(Box.createVerticalStrut(10));
        pnlInput.add(new JLabel("Mô tả chi tiết:"));
        txtDescription = new JTextArea(10, 20); // Tăng kích thước ô mô tả
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setPreferredSize(new Dimension(400, 150));
        pnlInput.add(scrollDesc);

        pnlLeft.add(pnlInput, BorderLayout.CENTER);

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

        // --- KHU VỰC DƯỚI CÙNG: NÚT ĐĂNG BÁN ---
        JPanel pnlBottom = new JPanel();

        btnSubmit = new JButton("ĐĂNG BÁN SẢN PHẨM MỚI");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(46, 204, 113));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setOpaque(true);
        btnSubmit.setBorderPainted(false);
        pnlBottom.add(btnSubmit);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---
        btnChooseImage.addActionListener(e -> chooseImage());
        btnSubmit.addActionListener(e -> submitProduct());
    }

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

    private void submitProduct() {
        if (txtName.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()
                || txtQuantity.getText().trim().isEmpty() || selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin và chọn ảnh!");
            return;
        }

        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());

            Product p = new Product(0, seller.getId(), txtName.getText().trim(), price, quantity, txtDescription.getText().trim(), "", "AVAILABLE");

            ProductDAO dao = new ProductDAO();
            if (dao.addProduct(p, selectedFile)) {
                JOptionPane.showMessageDialog(this, "Đăng bán thành công!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu dữ liệu vào Database.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Giá tiền và Số lượng phải là số hợp lệ!");
        }
    }
}