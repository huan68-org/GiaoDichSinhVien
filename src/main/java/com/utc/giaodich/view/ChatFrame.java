package com.utc.giaodich.view;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class ChatFrame extends JFrame {
    private JTextArea areaChat;
    private JTextField txtMessage;
    private WebSocketClient client;
    private String myUser;
    private String targetUser;

    public ChatFrame(String myUser, String targetUser) {
        this.myUser = myUser;
        this.targetUser = targetUser;

        setTitle("Chat với " + targetUser);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Khu vực hiển thị tin nhắn
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        // Khu vực nhập tin nhắn
        JPanel pnlBottom = new JPanel(new BorderLayout());
        txtMessage = new JTextField();
        JButton btnSend = new JButton("Gửi");
        pnlBottom.add(txtMessage, BorderLayout.CENTER);
        pnlBottom.add(btnSend, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // KẾT NỐI ĐẾN TỔNG ĐÀI
        connectToServer();

        // Xử lý sự kiện bấm nút Gửi
        btnSend.addActionListener(e -> sendMessage());
        // Cho phép ấn Enter để gửi luôn
        txtMessage.addActionListener(e -> sendMessage());
    }

    private void connectToServer() {
        try {
            client = new WebSocketClient(new URI("ws://localhost:8887")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    areaChat.append("Đã kết nối an toàn tới hệ thống...\n");
                    // Gửi một tin nhắn "chào sân" ẩn để Server biết mình là ai
                    client.send(myUser + "|server|has_joined");
                }

                @Override
                public void onMessage(String message) {
                    // Khi nhận được tin nhắn từ Server, in ra màn hình
                    areaChat.append(message + "\n");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {}

                @Override
                public void onError(Exception ex) {}
            };
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String msg = txtMessage.getText().trim();
        if (!msg.isEmpty() && client.isOpen()) {
            // Gửi lên Server theo đúng format: NgườiGửi|NgườiNhận|NộiDung
            client.send(myUser + "|" + targetUser + "|" + msg);
            areaChat.append("Tôi: " + msg + "\n");
            txtMessage.setText("");
        }
    }
}