package com.ptit.ltm.server.dao.impl;

import com.ptit.ltm.server.config.DatabaseConfig;
import com.ptit.ltm.server.dao.RoomDAO;
import com.ptit.ltm.common.model.Room;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RoomDAOImpl implements RoomDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    @Override
    public Room create(Room room) throws SQLException {
        String sql = "INSERT INTO room (code, name, description) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, room.getCode());
            stmt.setString(2, room.getName());
            stmt.setString(3, room.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm phòng thất bại, không có dòng nào được thêm");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Thêm phòng thất bại, không lấy được ID");
                }
            }
            
            log.info("Đã thêm phòng với ID: {}", room.getId());
            return room;
        }
    }

    @Override
    public Room update(Room room) throws SQLException {
        String sql = "UPDATE room SET code = ?, name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getCode());
            stmt.setString(2, room.getName());
            stmt.setString(3, room.getDescription());
            stmt.setInt(4, room.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cập nhật phòng thất bại, không có dòng nào được cập nhật");
            }
            
            log.info("Đã cập nhật phòng có ID: {}", room.getId());
            return findById(room.getId());
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM room WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            log.info("Đã xóa phòng có ID: {}, số dòng bị ảnh hưởng: {}", id, affectedRows);
            return affectedRows > 0;
        }
    }

    @Override
    public Room findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM room WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Room findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM room WHERE code = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Room> findAll() throws SQLException {
        String sql = "SELECT * FROM room";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        
        return rooms;
    }

    @Override
    public List<Room> searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM room WHERE name LIKE ? OR code LIKE ?";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
            log.info("Tìm thấy {} phòng với từ khóa: {}", rooms.size(), name);
        }
        
        return rooms;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setCode(rs.getString("code"));
        room.setName(rs.getString("name"));
        room.setDescription(rs.getString("description"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        room.setUpdatedAt(rs.getTimestamp("updated_at"));
        return room;
    }
}