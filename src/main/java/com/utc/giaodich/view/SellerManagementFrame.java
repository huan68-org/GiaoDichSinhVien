package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SellerManagementFrame extends JFrame {
    private User seller;
    private JTable table;
    private DefaultTableModel tableModel;

    public SellerManagementFrame(User seller) {
        this.seller = seller;
        setTitle("Quản lý gian hàng - " + seller.getFullName());
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel lblHeader = new JLabel("QUẢN LÝ BÀI ĐĂNG CỦA TÔI", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Tên sản phẩm", "Giá (VNĐ)", "Số lượng", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        // Ngăn không cho người dùng sửa trực tiếp trên bảng
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ chọn 1 dòng mỗi lần
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM DƯỚI CÙNG ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnDelete = new JButton("Xóa sản phẩm");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setOpaque(true); btnDelete.setBorderPainted(false);

        JButton btnEdit = new JButton("Sửa Nhanh (Giá/SL)");
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setOpaque(true); btnEdit.setBorderPainted(false);

        JButton btnInbox = new JButton("Hộp thư");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnAdd = new JButton("Đăng sản phẩm mới");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true); btnAdd.setBorderPainted(false);

        pnlBottom.add(btnInbox);
        pnlBottom.add(btnDelete); // Nút Xóa
        pnlBottom.add(btnEdit);   // Nút Sửa
        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnAdd);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        btnAdd.addActionListener(e -> {
            new SellerDashboard(seller).setVisible(true);
        });

        btnRefresh.addActionListener(e -> loadData());

        btnInbox.addActionListener(e -> {
            new ChatFrame(seller.getUsername(), "khach_hang").setVisible(true);
        });

        // Sự kiện Sửa Nhanh
        btnEdit.addActionListener(e -> editSelectedProduct());

        // Sự kiện Xóa
        btnDelete.addActionListener(e -> deleteSelectedProduct());

        loadData(); // Load dữ liệu lần đầu
    }

    private void loadData() {
        tableModel.setRowCount(0);
        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getProductsBySeller(seller.getId());

        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity(), p.getStatus()
            });
        }
    }

    // Hàm xử lý Xóa sản phẩm
    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trên bảng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy ID của sản phẩm ở cột 0
        int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String productName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm: " + productName + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ProductDAO dao = new ProductDAO();
            if (dao.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm thành công!");
                loadData(); // Tải lại bảng ngay lập tức
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Đã xảy ra lỗi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hàm xử lý Sửa nhanh
    private void editSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trên bảng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        double currentPrice = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
        int currentQty = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

        // Tạo giao diện nhập liệu nhỏ gọn bằng JOptionPane
        JTextField txtNewPrice = new JTextField(String.valueOf(currentPrice));
        JTextField txtNewQty = new JTextField(String.valueOf(currentQty));
        Object[] message = {
                "Cập nhật Giá bán (VNĐ):", txtNewPrice,
                "Cập nhật Số lượng:", txtNewQty
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Sửa nhanh sản phẩm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                double newPrice = Double.parseDouble(txtNewPrice.getText().trim());
                int newQty = Integer.parseInt(txtNewQty.getText().trim());

                if (newPrice < 0 || newQty < 0) {
                    JOptionPane.showMessageDialog(this, "Giá và số lượng không được âm!");
                    return;
                }

                ProductDAO dao = new ProductDAO();
                if(dao.updateProduct(productId, newPrice, newQty)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData(); // Tải lại bảng
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}