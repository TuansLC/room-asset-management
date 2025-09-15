package com.ptit.ltm.client.service;

import com.ptit.ltm.common.model.Request;
import com.ptit.ltm.common.model.RequestType;
import com.ptit.ltm.common.model.Response;
import com.ptit.ltm.common.model.Room;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class RoomService {
    private final TCPClient tcpClient;
    
    public RoomService() {
        this.tcpClient = TCPClient.getInstance();
    }
    
    public Room createRoom(Room room) throws Exception {
        log.info("Gửi yêu cầu tạo phòng: {}", room);
        Request request = new Request(RequestType.ADD_ROOM, room);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tạo phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        Room createdRoom = (Room) response.getData();
        log.info("Đã tạo phòng thành công: {}", createdRoom);
        return createdRoom;
    }
    
    public Room updateRoom(Room room) throws Exception {
        log.info("Gửi yêu cầu cập nhật phòng: {}", room);
        Request request = new Request(RequestType.UPDATE_ROOM, room);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi cập nhật phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        Room updatedRoom = (Room) response.getData();
        log.info("Đã cập nhật phòng thành công: {}", updatedRoom);
        return updatedRoom;
    }
    
    public void deleteRoom(Integer id) throws Exception {
        log.info("Gửi yêu cầu xóa phòng với ID: {}", id);
        Request request = new Request(RequestType.DELETE_ROOM, id);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi xóa phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        log.info("Đã xóa phòng thành công");
    }
    
    public Room getRoomById(Integer id) throws Exception {
        log.info("Tìm phòng theo ID: {}", id);
        Request request = new Request(RequestType.SEARCH_ROOM, id.toString());
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tìm phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        if (response.getData() == null) {
            log.error("Không có dữ liệu trả về cho ID: {}", id);
            return null;
        }

        List<Room> rooms = (List<Room>) response.getData();
        log.info("Dữ liệu trả về: {}", rooms);
        
        if (rooms.isEmpty()) {
            log.error("Không tìm thấy phòng với ID: {}", id);
            return null;
        }

        Room room = rooms.get(0);
        log.info("Đã tìm thấy phòng: {}", room);
        return room;
    }
    
    @SuppressWarnings("unchecked")
    public List<Room> getAllRooms() throws Exception {
        log.info("Lấy danh sách tất cả phòng");
        Request request = new Request(RequestType.SEARCH_ROOM, "");
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi lấy danh sách phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        List<Room> rooms = (List<Room>) response.getData();
        log.info("Đã lấy được {} phòng", rooms.size());
        return rooms;
    }
    
    @SuppressWarnings("unchecked")
    public List<Room> searchRooms(String query) throws Exception {
        log.info("Tìm kiếm phòng với từ khóa: {}", query);
        Request request = new Request(RequestType.SEARCH_ROOM, query);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tìm kiếm phòng: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        List<Room> rooms = (List<Room>) response.getData();
        log.info("Tìm thấy {} phòng", rooms.size());
        return rooms;
    }
}