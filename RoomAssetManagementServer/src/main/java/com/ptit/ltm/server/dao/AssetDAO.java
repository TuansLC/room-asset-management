package com.ptit.ltm.server.dao;

import com.ptit.ltm.common.model.Asset;
import java.sql.SQLException;
import java.util.List;

public interface AssetDAO {
    Asset create(Asset asset) throws SQLException;
    Asset update(Asset asset) throws SQLException;
    boolean delete(Integer id) throws SQLException;
    Asset findById(Integer id) throws SQLException;
    Asset findByCode(String code) throws SQLException;
    List<Asset> findAll() throws SQLException;
    List<Asset> searchByName(String name) throws SQLException;
}