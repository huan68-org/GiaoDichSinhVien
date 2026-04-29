package com.utc.giaodich.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ChatServer extends WebSocketServer {
    // Lưu trữ các user đang online và đường truyền của họ
    private static Map<String, WebSocket> onlineUsers = new HashMap<>();

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Một máy khách vừa kết nối tới máy chủ Chat.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Tin nhắn gửi lên có quy tắc: "NgườiGửi|NgườiNhận|NộiDung"
        // Ví dụ: "ha_buyer|huan_seller|Chào bạn, iPad còn không?"
        String[] parts = message.split("\\|");

        if (parts.length == 3) {
            String sender = parts[0];
            String receiver = parts[1];
            String content = parts[2];

            // Cập nhật người gửi vào danh sách online
            onlineUsers.put(sender, conn);

            // Tìm xem người nhận có đang online không
            WebSocket receiverConn = onlineUsers.get(receiver);
            if (receiverConn != null && receiverConn.isOpen()) {
                // Bắn tin nhắn sang máy người nhận
                receiverConn.send(sender + ": " + content);
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Xóa user khỏi danh sách nếu họ tắt app (Logic nâng cao có thể thêm sau)
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("=== HỆ THỐNG CHAT ĐANG CHẠY TẠI CỔNG 8887 ===");
    }

    public static void main(String[] args) {
        new ChatServer(8887).start();
    }
}