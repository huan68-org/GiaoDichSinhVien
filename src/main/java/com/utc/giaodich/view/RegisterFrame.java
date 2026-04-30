package com.utc.giaodich.view;

import com.utc.giaodich.dao.UserDAO;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField txtFullName, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnRegister, btnBack;

    public RegisterFrame() {
        setTitle("Sàn Đồ Cũ Sinh Viên UTC - Đăng ký");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Form nhập liệu
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 15));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        pnlInput.add(new JLabel("Họ và Tên:"));
        txtFullName = new JTextField();
        pnlInput.add(txtFullName);

        pnlInput.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        pnlInput.add(txtUsername);

        pnlInput.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        pnlInput.add(txtPassword);

        pnlInput.add(new JLabel("Bạn là:"));
        String[] roles = {"Người mua (BUYER)", "Người bán (SELLER)"};
        cbRole = new JComboBox<>(roles);
        pnlInput.add(cbRole);

        add(pnlInput, BorderLayout.CENTER);

        // 3. Khu vực Nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnRegister = new JButton("Đăng ký ngay");
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(false);

        btnBack = new JButton("Quay lại Đăng nhập");

        pnlButtons.add(btnRegister);
        pnlButtons.add(btnBack);
        add(pnlButtons, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        // Nút Đăng ký
        btnRegister.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            // Lấy Role từ ComboBox. Nếu chọn index 0 -> BUYER, index 1 -> SELLER
            String role = cbRole.getSelectedIndex() == 0 ? "BUYER" : "SELLER";

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserDAO dao = new UserDAO();
            // Tạo đối tượng User (ID truyền 0 vì DB sẽ tự tăng tự động)
            User newUser = new User(0, username, password, fullName, role);

            if (dao.register(newUser)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập nhé.");
                this.dispose(); // Đóng form Đăng ký
                new LoginFrame().setVisible(true); // Mở lại form Đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Quay lại
        btnBack.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }
}