package com.ptit.ltm.server.service;

import com.ptit.ltm.server.dao.AssetDAO;
import com.ptit.ltm.server.dao.impl.AssetDAOImpl;
import com.ptit.ltm.common.model.Asset;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class AssetService {
    private final AssetDAO assetDAO;

    public AssetService() {
        this.assetDAO = new AssetDAOImpl();
    }

    public Asset createAsset(Asset asset) throws Exception {
        validateAsset(asset);
        log.info("Tạo tài sản mới: {}", asset);
        Asset createdAsset = assetDAO.create(asset);
        log.info("Đã tạo tài sản thành công với ID: {}", createdAsset.getId());
        return createdAsset;
    }

    public Asset updateAsset(Asset asset) throws Exception {
        validateAsset(asset);
        if (asset.getId() == null) {
            throw new IllegalArgumentException("ID tài sản không được để trống khi cập nhật");
        }
        log.info("Cập nhật tài sản: {}", asset);
        Asset updatedAsset = assetDAO.update(asset);
        log.info("Đã cập nhật tài sản thành công với ID: {}", updatedAsset.getId());
        return updatedAsset;
    }

    public void deleteAsset(Integer id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("ID tài sản không được để trống");
        }
        log.info("Xóa tài sản với ID: {}", id);
        assetDAO.delete(id);
        log.info("Đã xóa tài sản thành công");
    }

    public Asset findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID tài sản không được để trống");
        }
        log.info("Tìm tài sản theo ID: {}", id);
        Asset asset = assetDAO.findById(id);
        log.info("Kết quả tìm kiếm: {}", asset);
        return asset;
    }

    public List<Asset> searchByName(String query) throws SQLException {
        log.info("Tìm tài sản theo tên: {}", query);
        List<Asset> assets = assetDAO.searchByName(query);
        log.info("Tìm thấy {} tài sản", assets.size());
        return assets;
    }

    private void validateAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Thông tin tài sản không được để trống");
        }
        if (asset.getCode() == null || asset.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tài sản không được để trống");
        }
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tài sản không được để trống");
        }
        if (asset.getValue() != null && asset.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá trị tài sản không được âm");
        }
    }
}