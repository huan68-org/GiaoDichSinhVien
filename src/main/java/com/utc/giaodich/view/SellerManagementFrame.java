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
    private Timer autoRefreshTimer; // Đã thêm lại bộ đếm giờ

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
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM DƯỚI CÙNG ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnDelete = new JButton("Xóa sản phẩm");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setOpaque(true); btnDelete.setBorderPainted(false);

        // Tìm đến pnlBottom và thêm nút mới
        JButton btnSales = new JButton("Lịch sử bán & Doanh thu 💰");
        btnSales.setBackground(new Color(155, 89, 182));
        btnSales.setForeground(Color.WHITE);
        btnSales.setOpaque(true); btnSales.setBorderPainted(false);
        btnSales.addActionListener(e -> new HistoryFrame("Thống kê bán hàng", seller.getId(), true).setVisible(true));
        pnlBottom.add(btnSales);

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
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnEdit);
        pnlBottom.add(btnRefresh);
        pnlBottom.add(btnAdd);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---
        btnAdd.addActionListener(e -> new SellerDashboard(seller).setVisible(true));

        btnRefresh.addActionListener(e -> loadData());

        // CHỈNH SỬA: Nhập chính xác username khách hàng để chat
        btnInbox.addActionListener(e -> {
            String targetBuyer = JOptionPane.showInputDialog(this,
                    "Nhập Username của khách hàng muốn Chat:",
                    "Hộp thư", JOptionPane.QUESTION_MESSAGE);

            if (targetBuyer != null && !targetBuyer.trim().isEmpty()) {
                new ChatFrame(seller.getUsername(), targetBuyer.trim()).setVisible(true);
            }
        });

        btnEdit.addActionListener(e -> editSelectedProduct());
        btnDelete.addActionListener(e -> deleteSelectedProduct());

        loadData();

        // --- KHÔI PHỤC AUTO-REFRESH GIỮ FOCUS ---
        autoRefreshTimer = new Timer(5000, e -> loadData());
        autoRefreshTimer.start();
    }

    private void loadData() {
        // Lưu lại vị trí đang chọn
        int selectedId = -1;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        }

        tableModel.setRowCount(0);
        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getProductsBySeller(seller.getId());

        int rowToSelect = -1;
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity(), p.getStatus()
            });
            if (p.getId() == selectedId) {
                rowToSelect = i;
            }
        }

        // Phục hồi vị trí đang chọn
        if (rowToSelect != -1) {
            table.setRowSelectionInterval(rowToSelect, rowToSelect);
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trên bảng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String productName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm: " + productName + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ProductDAO dao = new ProductDAO();
            if (dao.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Đã xảy ra lỗi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedProduct() {
        autoRefreshTimer.stop(); // Dừng timer lúc nhập liệu
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trên bảng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            autoRefreshTimer.start();
            return;
        }

        int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        double currentPrice = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
        int currentQty = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

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
                } else {
                    ProductDAO dao = new ProductDAO();
                    if(dao.updateProduct(productId, newPrice, newQty)) {
                        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                        loadData();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
        autoRefreshTimer.start(); // Bật lại timer sau khi sửa xong
    }
}