package com.ptit.ltm.client.ui.room;

import com.ptit.ltm.client.service.RoomService;
import com.ptit.ltm.common.model.Room;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Slf4j
public class RoomPanel extends JPanel {
    private final RoomService roomService;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public RoomPanel() {
        this.roomService = new RoomService();
        initComponents();
        loadRooms();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> searchRooms());
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table
        String[] columns = {"ID", "Mã phòng", "Tên phòng", "Mô tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm phòng");
        JButton editButton = new JButton("Sửa phòng");
        JButton deleteButton = new JButton("Xóa phòng");
        
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteRoom());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadRooms() {
        try {
            log.info("Đang tải danh sách phòng");
            List<Room> rooms = roomService.getAllRooms();
            updateTable(rooms);
            log.info("Đã tải {} phòng", rooms.size());
        } catch (Exception e) {
            log.error("Lỗi tải danh sách phòng: {}", e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Lỗi tải danh sách phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                room.getId(),
                room.getCode(),
                room.getName(),
                room.getDescription()
            });
        }
    }
    
    private void searchRooms() {
        try {
            String query = searchField.getText().trim();
            log.info("Tìm kiếm phòng với từ khóa: {}", query);
            List<Room> rooms = roomService.searchRooms(query);
            updateTable(rooms);
            log.info("Tìm thấy {} phòng", rooms.size());
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm phòng: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tìm kiếm phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        RoomDialog dialog = new RoomDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadRooms();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phòng cần sửa",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            log.info("Đang tải thông tin phòng có ID: {}", id);
            
            Room room = roomService.getRoomById(id);
            if (room == null) {
                log.error("Không tìm thấy phòng có ID: {}", id);
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin phòng",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log.info("Mở form sửa phòng: {}", room);
            RoomDialog dialog = new RoomDialog(SwingUtilities.getWindowAncestor(this), room);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                log.info("Đã cập nhật phòng, tải lại dữ liệu");
                loadRooms();
            }
        } catch (Exception e) {
            log.error("Lỗi tải thông tin phòng: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tải thông tin phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phòng cần xóa",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String code = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa phòng " + code + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                log.info("Xóa phòng có ID: {}", id);
                roomService.deleteRoom(id);
                log.info("Đã xóa phòng thành công");
                loadRooms();
            } catch (Exception e) {
                log.error("Lỗi xóa phòng: {}", e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Lỗi xóa phòng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}