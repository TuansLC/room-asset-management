package com.ptit.ltm.server.socket;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TCPServer {
    private final int port;
    private final ExecutorService executorService;
    private boolean running;

    public TCPServer(int port) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void start() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Máy chủ đã khởi động tại cổng {}", port);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                log.info("Client mới kết nối: {}", clientSocket.getInetAddress());
                executorService.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            log.error("Lỗi máy chủ: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        executorService.shutdown();
        log.info("Máy chủ đã dừng");
    }
}