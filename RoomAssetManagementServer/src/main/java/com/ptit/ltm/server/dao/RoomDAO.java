package com.ptit.ltm.server.dao;

import com.ptit.ltm.common.model.Room;
import java.sql.SQLException;
import java.util.List;

public interface RoomDAO {
    Room create(Room room) throws SQLException;
    Room update(Room room) throws SQLException;
    boolean delete(Integer id) throws SQLException;
    Room findById(Integer id) throws SQLException;
    Room findByCode(String code) throws SQLException;
    List<Room> findAll() throws SQLException;
    List<Room> searchByName(String name) throws SQLException;
}
