package com.ptit.ltm.server.service;

import com.ptit.ltm.server.dao.RoomDAO;
import com.ptit.ltm.server.dao.impl.RoomDAOImpl;
import com.ptit.ltm.common.model.Room;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class RoomService {
    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAOImpl();
    }

    public Room createRoom(Room room) throws Exception {
        validateRoom(room);
        log.info("Tạo phòng mới: {}", room);
        Room createdRoom = roomDAO.create(room);
        log.info("Đã tạo phòng thành công với ID: {}", createdRoom.getId());
        return createdRoom;
    }

    public Room updateRoom(Room room) throws Exception {
        validateRoom(room);
        if (room.getId() == null) {
            throw new IllegalArgumentException("ID phòng không được để trống khi cập nhật");
        }
        log.info("Cập nhật phòng: {}", room);
        Room updatedRoom = roomDAO.update(room);
        log.info("Đã cập nhật phòng thành công với ID: {}", updatedRoom.getId());
        return updatedRoom;
    }

    public void deleteRoom(Integer id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("ID phòng không được để trống");
        }
        log.info("Xóa phòng với ID: {}", id);
        roomDAO.delete(id);
        log.info("Đã xóa phòng thành công");
    }

    public Room findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID phòng không được để trống");
        }
        log.info("Tìm phòng theo ID: {}", id);
        Room room = roomDAO.findById(id);
        log.info("Kết quả tìm kiếm: {}", room);
        return room;
    }

    public List<Room> searchByName(String query) throws SQLException {
        log.info("Tìm phòng theo tên: {}", query);
        List<Room> rooms = roomDAO.searchByName(query);
        log.info("Tìm thấy {} phòng", rooms.size());
        return rooms;
    }

    private void validateRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Thông tin phòng không được để trống");
        }
        if (room.getCode() == null || room.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống");
        }
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }
    }
}