package com.utc.giaodich.view;

import com.utc.giaodich.controller.OrderController;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class CheckoutFrame extends JFrame {
    private User buyer;
    private Product product;

    private JTextField txtPhone, txtAddress;
    private JComboBox<String> cbPaymentMethod;
    private JSpinner spnQuantity;
    private JLabel lblTotalAmount; // Hiện tổng tiền thay đổi linh hoạt

    public CheckoutFrame(User buyer, Product product) {
        this.buyer = buyer;
        this.product = product;

        setTitle("Thanh toán an toàn - Sàn Đồ Cũ UTC");
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Chỉ đóng form này, không tắt app
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Định dạng tiền tệ cho đẹp mắt
        DecimalFormat df = new DecimalFormat("#,###.## VNĐ");

        // --- TIÊU ĐỀ ---
        JLabel lblHeader = new JLabel("XÁC NHẬN ĐƠN HÀNG", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- KHU VỰC GIỮA (Thông tin & Nhập liệu) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        // Tóm tắt sản phẩm
        JPanel pnlSummary = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt sản phẩm"));
        pnlSummary.add(new JLabel("Sản phẩm: " + product.getName()));
        pnlSummary.add(new JLabel("Đơn giá: " + df.format(product.getPrice())));

        lblTotalAmount = new JLabel("Tổng thanh toán: " + df.format(product.getPrice()));
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalAmount.setForeground(Color.RED);
        pnlSummary.add(lblTotalAmount);

        pnlCenter.add(pnlSummary, BorderLayout.NORTH);

        // Nhập thông tin giao hàng
        JPanel pnlShipping = new JPanel(new GridLayout(4, 2, 10, 15)); // 4 hàng để chứa ô số lượng
        pnlShipping.setBorder(BorderFactory.createTitledBorder("Thông tin giao hàng"));

        pnlShipping.add(new JLabel("Số điện thoại (*):"));
        txtPhone = new JTextField();
        pnlShipping.add(txtPhone);

        pnlShipping.add(new JLabel("Địa chỉ nhận hàng (*):"));
        txtAddress = new JTextField();
        pnlShipping.add(txtAddress);

        pnlShipping.add(new JLabel("Số lượng muốn mua:"));
        // Cấu hình Spinner: Mặc định là 1, Min là 1, Max là số lượng trong kho, bước nhảy là 1
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, product.getQuantity(), 1);
        spnQuantity = new JSpinner(spinnerModel);
        pnlShipping.add(spnQuantity);

        pnlShipping.add(new JLabel("Phương thức TT:"));
        String[] methods = {"Thanh toán khi nhận hàng (COD)", "Ví MoMo", "Chuyển khoản Ngân hàng"};
        cbPaymentMethod = new JComboBox<>(methods);
        pnlShipping.add(cbPaymentMethod);

        pnlCenter.add(pnlShipping, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- KHU VỰC DƯỚI (Nút bấm) ---
        JPanel pnlBottom = new JPanel();
        JButton btnConfirm = new JButton("XÁC NHẬN MUA HÀNG");
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(46, 204, 113));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setOpaque(true);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setPreferredSize(new Dimension(250, 40));
        pnlBottom.add(btnConfirm);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        // Lắng nghe sự kiện thay đổi số lượng để tính lại tiền ngay lập tức
        spnQuantity.addChangeListener(e -> {
            int qty = (int) spnQuantity.getValue();
            double total = qty * product.getPrice();
            lblTotalAmount.setText("Tổng thanh toán: " + df.format(total));
        });

        // Bắt sự kiện nút xác nhận
        btnConfirm.addActionListener(e -> confirmOrder());
    }

    private void confirmOrder() {
        int buyQty = (int) spnQuantity.getValue();
        double total = buyQty * product.getPrice();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();

        // Giao việc cho Controller xử lý logic
        OrderController controller = new OrderController();
        controller.handleCheckout(
                buyer.getId(),
                product.getId(),
                buyQty,
                total,
                phone,
                address,
                paymentMethod,
                () -> this.dispose() // Đóng cửa sổ nếu Controller báo mua thành công
        );
    }
}