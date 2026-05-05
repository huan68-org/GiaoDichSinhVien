package com.utc.giaodich.view;

import com.utc.giaodich.dao.MessageDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame extends JFrame {
    private String currentUser;
    private String partnerUser;

    private JTextArea txtChatArea;
    private JTextField txtMessage;
    private MessageDAO messageDAO;
    private Timer chatTimer; // Bộ đếm giờ để cập nhật tin nhắn liên tục

    public ChatFrame(String currentUser, String partnerUser) {
        this.currentUser = currentUser;
        this.partnerUser = partnerUser;
        this.messageDAO = new MessageDAO();

        setTitle("Chat: " + currentUser + " ↔ " + partnerUser);
        setSize(400, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- KHU VỰC HIỂN THỊ TIN NHẮN ---
        txtChatArea = new JTextArea();
        txtChatArea.setEditable(false);
        txtChatArea.setLineWrap(true);
        txtChatArea.setWrapStyleWord(true);
        txtChatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        txtChatArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(txtChatArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- KHU VỰC NHẬP TIN NHẮN DƯỚI CÙNG ---
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtMessage = new JTextField();
        txtMessage.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnSend = new JButton("Gửi");
        btnSend.setBackground(new Color(52, 152, 219));
        btnSend.setForeground(Color.WHITE);
        btnSend.setOpaque(true);
        btnSend.setBorderPainted(false);

        pnlBottom.add(txtMessage, BorderLayout.CENTER);
        pnlBottom.add(btnSend, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN GỬI TIN NHẮN ---
        // Gửi bằng nút bấm
        btnSend.addActionListener(e -> sendMessage());

        // Gửi bằng phím Enter cho chuyên nghiệp
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // --- REAL-TIME ENGINE (Load lại tin nhắn mỗi 1 giây) ---
        loadChatHistory(); // Load lần đầu khi mở form

        chatTimer = new Timer(1000, e -> loadChatHistory());
        chatTimer.start();

        // Tắt Timer khi đóng cửa sổ Chat để tiết kiệm tài nguyên
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                chatTimer.stop();
            }
        });
    }

    // Hàm thực hiện việc gửi tin nhắn
    private void sendMessage() {
        String msg = txtMessage.getText().trim();
        if (!msg.isEmpty()) {
            if (messageDAO.sendMessage(currentUser, partnerUser, msg)) {
                txtMessage.setText(""); // Xóa trắng ô nhập sau khi gửi
                loadChatHistory();      // Nạp lại màn hình chat ngay lập tức
            }
        }
    }

    // Hàm lấy lịch sử chat từ Database và cập nhật lên màn hình
    private void loadChatHistory() {
        String newHistory = messageDAO.getChatHistory(currentUser, partnerUser);

        // Chỉ cập nhật giao diện nếu có tin nhắn mới (tránh bị giật màn hình)
        if (!txtChatArea.getText().equals(newHistory)) {
            txtChatArea.setText(newHistory);
            // Tự động cuộn xuống dòng cuối cùng để đọc tin mới
            txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());
        }
    }
}