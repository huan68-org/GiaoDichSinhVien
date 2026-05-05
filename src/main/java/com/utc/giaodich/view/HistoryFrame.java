package com.utc.giaodich.view;

import com.utc.giaodich.dao.ProductDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class HistoryFrame extends JFrame {
    public HistoryFrame(String title, int userId, boolean isSeller) {
        setTitle(title);
        setSize(750, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] cols = isSeller ? new String[]{"Tên Khách hàng", "Số lượng", "Tổng tiền giao dịch", "Ngày giao dịch"}
                : new String[]{"Tên sản phẩm", "Số lượng", "Số tiền đã trả", "Ngày mua"};

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        ProductDAO dao = new ProductDAO();
        List<Object[]> data = isSeller ? dao.getSalesHistory(userId) : dao.getPurchaseHistory(userId);

        double rawTotal = 0;
        DecimalFormat df = new DecimalFormat("#,### VNĐ");

        for (Object[] row : data) {
            model.addRow(row);
            rawTotal += (double) row[2];
        }

        if (isSeller) {
            double commission = rawTotal * 0.1; // Sàn thu 10% phí[cite: 6]
            double netProfit = rawTotal - commission;

            JPanel pnlProfit = new JPanel(new GridLayout(3, 1));
            pnlProfit.setBackground(new Color(236, 240, 241));
            pnlProfit.setBorder(BorderFactory.createTitledBorder("BÁO CÁO DOANH THU THỰC NHẬN"));
            pnlProfit.add(new JLabel("  Doanh thu tổng: " + df.format(rawTotal)));
            pnlProfit.add(new JLabel("  Phí sàn duy trì (10%): -" + df.format(commission)));
            JLabel lblNet = new JLabel("  LỢI NHUẬN CUỐI CÙNG: " + df.format(netProfit));
            lblNet.setForeground(new Color(192, 57, 43));
            lblNet.setFont(new Font("Arial", Font.BOLD, 18));
            pnlProfit.add(lblNet);
            add(pnlProfit, BorderLayout.SOUTH);
        }
    }
}