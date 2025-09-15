package com.ptit.ltm.server.socket;

import com.ptit.ltm.common.model.*;
import com.ptit.ltm.server.service.*;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final RoomService roomService;
    private final AssetService assetService;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.roomService = new RoomService();
        this.assetService = new AssetService();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            while (!clientSocket.isClosed()) {
                Request request = (Request) in.readObject();
                log.info("Nhận yêu cầu: {}", request.getType());
                Response response = handleRequest(request);
                log.info("Gửi phản hồi: thành công={}, dữ liệu={}", response.isSuccess(), response.getData());
                out.writeObject(response);
                out.flush();
            }
        } catch (EOFException e) {
            log.info("Client đã ngắt kết nối");
        } catch (IOException | ClassNotFoundException e) {
            log.error("Lỗi xử lý client: {}", e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private Response handleRequest(Request request) {
        try {
            log.info("Đang xử lý yêu cầu: {}", request.getType());
            
            switch (request.getType()) {
                case ADD_ROOM -> {
                    Room newRoom = (Room) request.getData();
                    return new Response(true, roomService.createRoom(newRoom), "Thêm phòng thành công");
                }
                
                case UPDATE_ROOM -> {
                    Room updatedRoom = (Room) request.getData();
                    return new Response(true, roomService.updateRoom(updatedRoom), "Cập nhật phòng thành công");
                }
                
                case DELETE_ROOM -> {
                    Integer roomId = (Integer) request.getData();
                    roomService.deleteRoom(roomId);
                    return new Response(true, null, "Xóa phòng thành công");
                }
                
                case SEARCH_ROOM -> {
                    String roomQuery = (String) request.getData();
                    log.info("Tìm kiếm phòng với từ khóa: {}", roomQuery);
                    
                    if (roomQuery.matches("\\d+")) {
                        Integer searchRoomId = Integer.valueOf(roomQuery);
                        log.info("Tìm phòng theo ID: {}", searchRoomId);
                        Room room = roomService.findById(searchRoomId);
                        log.info("Kết quả tìm kiếm phòng: {}", room);
                        
                        if (room != null) {
                            return new Response(true, Collections.singletonList(room), "Tìm thấy phòng");
                        } else {
                            return new Response(false, null, "Không tìm thấy phòng");
                        }
                    } else {
                        List<Room> rooms = roomService.searchByName(roomQuery);
                        log.info("Tìm thấy {} phòng", rooms.size());
                        return new Response(true, rooms, "Tìm thấy danh sách phòng");
                    }
                }
                
                case ADD_ASSET -> {
                    Asset newAsset = (Asset) request.getData();
                    return new Response(true, assetService.createAsset(newAsset), "Thêm tài sản thành công");
                }
                
                case UPDATE_ASSET -> {
                    Asset updatedAsset = (Asset) request.getData();
                    return new Response(true, assetService.updateAsset(updatedAsset), "Cập nhật tài sản thành công");
                }
                
                case DELETE_ASSET -> {
                    Integer assetId = (Integer) request.getData();
                    assetService.deleteAsset(assetId);
                    return new Response(true, null, "Xóa tài sản thành công");
                }
                
                case SEARCH_ASSET -> {
                    String assetQuery = (String) request.getData();
                    log.info("Tìm kiếm tài sản với từ khóa: {}", assetQuery);
                    
                    if (assetQuery.matches("\\d+")) {
                        Integer searchAssetId = Integer.valueOf(assetQuery);
                        log.info("Tìm tài sản theo ID: {}", searchAssetId);
                        Asset asset = assetService.findById(searchAssetId);
                        log.info("Kết quả tìm kiếm tài sản: {}", asset);
                        
                        if (asset != null) {
                            return new Response(true, Collections.singletonList(asset), "Tìm thấy tài sản");
                        } else {
                            return new Response(false, null, "Không tìm thấy tài sản");
                        }
                    } else {
                        List<Asset> assets = assetService.searchByName(assetQuery);
                        log.info("Tìm thấy {} tài sản", assets.size());
                        return new Response(true, assets, "Tìm thấy danh sách tài sản");
                    }
                }
                
                default -> {
                    log.warn("Không xác định được loại yêu cầu: {}", request.getType());
                    return new Response(false, null, "Loại yêu cầu không hợp lệ");
                }
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý yêu cầu: {}", e.getMessage(), e);
            return new Response(false, null, "Lỗi: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            log.info("Đã đóng kết nối cho client: {}", clientSocket.getInetAddress());
        } catch (IOException e) {
            log.error("Lỗi đóng kết nối: {}", e.getMessage());
        }
    }
}