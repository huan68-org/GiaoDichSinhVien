package com.utc.giaodich.view;

import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class CheckoutFrame extends JFrame {
    private User buyer;
    private Product product;

    private JTextField txtPhone, txtAddress;
    private JComboBox<String> cbPaymentMethod;
    private JButton btnConfirm;

    public CheckoutFrame(User buyer, Product product) {
        this.buyer = buyer;
        this.product = product;

        setTitle("Thanh toán an toàn - Sàn Đồ Cũ UTC");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245)); // Màu nền xám nhạt Apple

        DecimalFormat df = new DecimalFormat("#,### VNĐ");

        // --- PHẦN 1: HEADER ---
        JLabel lblHeader = new JLabel("XÁC NHẬN ĐƠN HÀNG", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeader.setForeground(new Color(44, 62, 80));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- PHẦN 2: THÔNG TIN SẢN PHẨM & FORM NHẬP LIỆU ---
        JPanel pnlCenter = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlCenter.setBackground(new Color(245, 245, 245));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // 2.1 Box Tóm tắt đơn hàng
        JPanel pnlOrderSummary = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlOrderSummary.setBackground(Color.WHITE);
        pnlOrderSummary.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Tóm tắt sản phẩm", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(41, 128, 185)
        ));

        JLabel lblProductName = new JLabel(" Sản phẩm: " + product.getName());
        lblProductName.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblProductPrice = new JLabel(" Tổng thanh toán: " + df.format(product.getPrice()));
        lblProductPrice.setFont(new Font("Arial", Font.BOLD, 16));
        lblProductPrice.setForeground(new Color(231, 76, 60));

        pnlOrderSummary.add(lblProductName);
        pnlOrderSummary.add(lblProductPrice);
        pnlCenter.add(pnlOrderSummary);

        // 2.2 Box Thông tin giao hàng
        JPanel pnlShipping = new JPanel(new GridLayout(3, 2, 10, 15));
        pnlShipping.setBackground(Color.WHITE);
        pnlShipping.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Thông tin giao hàng", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(41, 128, 185)
        ));

        pnlShipping.add(new JLabel(" Số điện thoại:"));
        txtPhone = new JTextField();
        pnlShipping.add(txtPhone);

        pnlShipping.add(new JLabel(" Địa chỉ nhận hàng:"));
        txtAddress = new JTextField();
        pnlShipping.add(txtAddress);

        pnlShipping.add(new JLabel(" Phương thức TT:"));
        String[] methods = {"Thanh toán khi nhận hàng (COD)", "Chuyển khoản Ngân hàng", "Ví MoMo"};
        cbPaymentMethod = new JComboBox<>(methods);
        cbPaymentMethod.setBackground(Color.WHITE);
        pnlShipping.add(cbPaymentMethod);

        pnlCenter.add(pnlShipping);
        add(pnlCenter, BorderLayout.CENTER);

        // --- PHẦN 3: NÚT XÁC NHẬN ĐẶT HÀNG ---
        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(new Color(245, 245, 245));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnConfirm = new JButton("XÁC NHẬN MUA HÀNG");
        btnConfirm.setPreferredSize(new Dimension(300, 45));
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirm.setBackground(new Color(39, 174, 96)); // Xanh lá cây tín nhiệm
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setOpaque(true);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Xử lý sự kiện đặt hàng
        btnConfirm.addActionListener(e -> confirmOrder());

        pnlBottom.add(btnConfirm);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void confirmOrder() {
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Số điện thoại và Địa chỉ giao hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạm thời giả lập việc lưu đơn hàng thành công (Sẽ code DB ở bước sau)
        String method = (String) cbPaymentMethod.getSelectedItem();
        String msg = "Chúc mừng " + buyer.getFullName() + " đã đặt hàng thành công!\n\n"
                + "Sản phẩm: " + product.getName() + "\n"
                + "Thanh toán qua: " + method + "\n"
                + "Đơn hàng sẽ được giao đến: " + address;

        JOptionPane.showMessageDialog(this, msg, "Đặt hàng thành công", JOptionPane.INFORMATION_MESSAGE);

        // Đóng cửa sổ thanh toán sau khi mua xong
        this.dispose();
    }
}