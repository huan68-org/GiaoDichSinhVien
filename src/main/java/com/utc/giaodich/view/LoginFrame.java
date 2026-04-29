package com.utc.giaodich.view;

import com.utc.giaodich.dao.UserDAO;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Sàn Đồ Cũ Sinh Viên UTC - Đăng nhập");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Cho cửa sổ ra giữa màn hình
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
        JPanel pnlButton = new JPanel();
        btnLogin = new JButton("Đăng nhập vào hệ thống");
        pnlButton.add(btnLogin);
        add(pnlButton);

        // --- XỬ LÝ SỰ KIỆN PHÂN VAI ---
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            UserDAO userDAO = new UserDAO();
            User loggedInUser = userDAO.login(user, pass); // Gọi hàm ở Bước 1

            if (loggedInUser != null) {
                // Đăng nhập thành công -> Đóng cửa sổ Login
                this.dispose();

                // KIỂM TRA ROLE ĐỂ MỞ ĐÚNG GIAO DIỆN
                if ("SELLER".equals(loggedInUser.getRole())) {
                    // Truyền đối tượng user sang giao diện bán hàng để nó biết ai đang đăng
                    new SellerDashboard(loggedInUser).setVisible(true);

                } else if ("BUYER".equals(loggedInUser.getRole())) {
                    new BuyerDashboard(loggedInUser).setVisible(true);

                } 
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi truy cập", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        // Giao diện sẽ tự khởi chạy ở đây
        new LoginFrame().setVisible(true);
    }
}