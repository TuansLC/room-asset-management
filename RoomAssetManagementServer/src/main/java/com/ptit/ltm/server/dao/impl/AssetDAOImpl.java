package com.ptit.ltm.server.dao.impl;

import com.ptit.ltm.server.config.DatabaseConfig;
import com.ptit.ltm.server.dao.AssetDAO;
import com.ptit.ltm.common.model.Asset;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AssetDAOImpl implements AssetDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    @Override
    public Asset create(Asset asset) throws SQLException {
        String sql = "INSERT INTO asset (code, name, type, room_id, value) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, asset.getCode());
            stmt.setString(2, asset.getName());
            stmt.setString(3, asset.getType());
            stmt.setObject(4, asset.getRoomId());
            stmt.setBigDecimal(5, asset.getValue());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm tài sản thất bại, không có dòng nào được thêm");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    asset.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Thêm tài sản thất bại, không lấy được ID");
                }
            }
            
            log.info("Đã thêm tài sản với ID: {}", asset.getId());
            return asset;
        }
    }

    @Override
    public Asset update(Asset asset) throws SQLException {
        String sql = "UPDATE asset SET code = ?, name = ?, type = ?, room_id = ?, value = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, asset.getCode());
            stmt.setString(2, asset.getName());
            stmt.setString(3, asset.getType());
            stmt.setObject(4, asset.getRoomId());
            stmt.setBigDecimal(5, asset.getValue());
            stmt.setInt(6, asset.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cập nhật tài sản thất bại, không có dòng nào được cập nhật");
            }
            
            log.info("Đã cập nhật tài sản có ID: {}", asset.getId());
            return findById(asset.getId());
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM asset WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            log.info("Đã xóa tài sản có ID: {}, số dòng bị ảnh hưởng: {}", id, affectedRows);
            return affectedRows > 0;
        }
    }

    @Override
    public Asset findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM asset WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAsset(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Asset findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM asset WHERE code = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAsset(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Asset> findAll() throws SQLException {
        String sql = "SELECT * FROM asset";
        List<Asset> assets = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                assets.add(mapResultSetToAsset(rs));
            }
        }
        
        return assets;
    }

    @Override
    public List<Asset> searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM asset WHERE name LIKE ? OR code LIKE ?";
        List<Asset> assets = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assets.add(mapResultSetToAsset(rs));
                }
            }
            log.info("Tìm thấy {} tài sản với từ khóa: {}", assets.size(), name);
        }
        
        return assets;
    }

    private Asset mapResultSetToAsset(ResultSet rs) throws SQLException {
        Asset asset = new Asset();
        asset.setId(rs.getInt("id"));
        asset.setCode(rs.getString("code"));
        asset.setName(rs.getString("name"));
        asset.setType(rs.getString("type"));
        asset.setRoomId(rs.getObject("room_id", Integer.class));
        asset.setValue(rs.getBigDecimal("value"));
        asset.setCreatedAt(rs.getTimestamp("created_at"));
        asset.setUpdatedAt(rs.getTimestamp("updated_at"));
        return asset;
    }
}