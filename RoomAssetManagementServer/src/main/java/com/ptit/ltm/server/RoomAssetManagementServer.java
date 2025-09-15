package com.ptit.ltm.server;

import com.ptit.ltm.server.socket.TCPServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoomAssetManagementServer {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        log.info("Đang khởi động Hệ thống Quản lý Tài sản Phòng...");
        TCPServer server = new TCPServer(PORT);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Đang tắt máy chủ...");
            server.stop();
        }));

        // Start server
        server.start();
    }
}