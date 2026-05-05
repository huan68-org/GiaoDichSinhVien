package com.utc.giaodich.view;

import com.utc.giaodich.model.Product;
import com.utc.giaodich.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceFrame extends JFrame {

    private JPanel pnlReceipt; // Panel chứa nội dung tờ hóa đơn để in

    public InvoiceFrame(User buyer, Product product, int quantity, double totalAmount, String paymentMethod) {
        setTitle("Hóa đơn Điện tử");
        setSize(450, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- KHU VỰC TỜ HÓA ĐƠN (NỀN TRẮNG) ---
        pnlReceipt = new JPanel();
        pnlReceipt.setLayout(new BoxLayout(pnlReceipt, BoxLayout.Y_AXIS));
        pnlReceipt.setBackground(Color.WHITE);
        pnlReceipt.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Tiêu đề hóa đơn
        JLabel lblBrand = new JLabel("SÀN ĐỒ CŨ UTC", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("HÓA ĐƠN MUA HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlReceipt.add(lblBrand);
        pnlReceipt.add(Box.createVerticalStrut(5));
        pnlReceipt.add(lblTitle);
        pnlReceipt.add(Box.createVerticalStrut(20));

        // Nét đứt phân cách
        JLabel lblDashedLine = new JLabel("---------------------------------------------------------");
        lblDashedLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlReceipt.add(lblDashedLine);
        pnlReceipt.add(Box.createVerticalStrut(15));

        DecimalFormat df = new DecimalFormat("#,###.## VNĐ");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentTime = sdf.format(new Date());

        // Thông tin chi tiết
        pnlReceipt.add(createRow("Mã KH:", buyer.getUsername()));
        pnlReceipt.add(createRow("Thời gian:", currentTime));
        pnlReceipt.add(createRow("Phương thức:", paymentMethod));
        pnlReceipt.add(Box.createVerticalStrut(15));

        pnlReceipt.add(createRow("Sản phẩm:", product.getName()));
        pnlReceipt.add(createRow("Đơn giá:", df.format(product.getPrice())));
        pnlReceipt.add(createRow("Số lượng:", String.valueOf(quantity)));

        pnlReceipt.add(Box.createVerticalStrut(15));
        JLabel lblDashedLine2 = new JLabel("---------------------------------------------------------");
        lblDashedLine2.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlReceipt.add(lblDashedLine2);
        pnlReceipt.add(Box.createVerticalStrut(10));

        // Tổng tiền
        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setBackground(Color.WHITE);
        JLabel lblTotalText = new JLabel("TỔNG THANH TOÁN:");
        lblTotalText.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblTotalValue = new JLabel(df.format(totalAmount));
        lblTotalValue.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalValue.setForeground(Color.RED);
        pnlTotal.add(lblTotalText, BorderLayout.WEST);
        pnlTotal.add(lblTotalValue, BorderLayout.EAST);
        pnlReceipt.add(pnlTotal);

        // Lời cảm ơn
        pnlReceipt.add(Box.createVerticalStrut(30));
        JLabel lblThankYou = new JLabel("Cảm ơn bạn đã tin tưởng Sàn đồ cũ UTC!");
        lblThankYou.setFont(new Font("Arial", Font.ITALIC, 12));
        lblThankYou.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlReceipt.add(lblThankYou);

        add(new JScrollPane(pnlReceipt), BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnExportPDF = new JButton("In / Xuất file PDF");
        btnExportPDF.setBackground(new Color(231, 76, 60));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setOpaque(true); btnExportPDF.setBorderPainted(false);

        JButton btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(52, 152, 219));
        btnClose.setForeground(Color.WHITE);
        btnClose.setOpaque(true); btnClose.setBorderPainted(false);

        // Logic xuất PDF siêu mượt bằng đồ họa của Java
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnClose.addActionListener(e -> this.dispose());

        pnlFooter.add(btnExportPDF);
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private JPanel createRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(400, 25));

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Arial", Font.PLAIN, 13));
        lblName.setForeground(Color.DARK_GRAY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Arial", Font.BOLD, 13));

        panel.add(lblName, BorderLayout.WEST);
        panel.add(lblVal, BorderLayout.EAST);
        return panel;
    }

    // Hàm gọi máy in ảo của Hệ điều hành để lưu ra file PDF
    private void exportToPDF() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                // Di chuyển đồ họa vào vùng có thể in
                g2d.translate(pageFormat.getImageableX() + 20, pageFormat.getImageableY() + 20);

                // Vẽ toàn bộ giao diện tờ hóa đơn (pnlReceipt) lên tờ giấy in
                pnlReceipt.printAll(graphics);
                return PAGE_EXISTS;
            }
        });

        // Bật hộp thoại in của MacOS/Windows
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + ex.getMessage());
            }
        }
    }
}