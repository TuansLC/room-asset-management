package com.ptit.ltm.client.service;

import com.ptit.ltm.common.model.Request;
import com.ptit.ltm.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.Socket;

@Slf4j
public class TCPClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private static TCPClient instance;
    
    private TCPClient() {}
    
    public static TCPClient getInstance() {
        if (instance == null) {
            instance = new TCPClient();
        }
        return instance;
    }
    
    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        log.info("Đã kết nối đến server {}:{}", SERVER_HOST, SERVER_PORT);
    }
    
    public Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        if (socket == null || socket.isClosed()) {
            log.info("Đang kết nối lại với server...");
            connect();
        }
        
        log.info("Gửi yêu cầu: {}", request.getType());
        out.writeObject(request);
        out.flush();
        
        Response response = (Response) in.readObject();
        log.info("Nhận phản hồi: thành công={}, thông điệp={}", response.isSuccess(), response.getMessage());
        return response;
    }
    
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            log.info("Đã ngắt kết nối với server");
        } catch (IOException e) {
            log.error("Lỗi khi ngắt kết nối: {}", e.getMessage());
        }
    }
}