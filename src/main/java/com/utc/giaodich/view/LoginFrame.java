package com.utc.giaodich.view;

import com.utc.giaodich.dao.UserDAO;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoToRegister;

    public LoginFrame() {
        setTitle("Sàn Đồ Cũ Sinh Viên UTC - Đăng nhập");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("CHÀO MỪNG ĐẾN VỚI SÀN GIAO DỊCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle);

        // 2. Form nhập liệu
        JPanel pnlInput = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        pnlInput.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        pnlInput.add(txtUsername);

        pnlInput.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        pnlInput.add(txtPassword);
        add(pnlInput);

        // 3. Nút bấm
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng nhập");
        btnGoToRegister = new JButton("Tạo tài khoản");

        pnlButton.add(btnLogin);
        pnlButton.add(btnGoToRegister);
        add(pnlButton);

        // --- XỬ LÝ SỰ KIỆN ---

        // Nút chuyển sang Đăng ký
        btnGoToRegister.addActionListener(e -> {
            this.dispose();
            new RegisterFrame().setVisible(true);
        });

        // Nút Đăng nhập
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            UserDAO userDAO = new UserDAO();
            User loggedInUser = userDAO.login(user, pass);

            if (loggedInUser != null) {
                // Đăng nhập thành công -> Đóng cửa sổ Login
                this.dispose();

                // FIX LỖI Ở ĐÂY: Phân luồng chính xác
                if ("SELLER".equals(loggedInUser.getRole())) {
                    // Mở TRANG QUẢN LÝ thay vì mở trực tiếp form Đăng bài
                    new SellerManagementFrame(loggedInUser).setVisible(true);

                } else if ("BUYER".equals(loggedInUser.getRole())) {
                    new BuyerDashboard(loggedInUser).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi truy cập", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}