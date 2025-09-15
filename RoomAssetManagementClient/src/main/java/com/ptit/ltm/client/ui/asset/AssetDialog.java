package com.ptit.ltm.client.ui.asset;

import com.ptit.ltm.client.service.AssetService;
import com.ptit.ltm.client.service.RoomService;
import com.ptit.ltm.common.model.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class AssetDialog extends JDialog {
    private final AssetService assetService;
    private final RoomService roomService;
    private final Asset asset;
    
    private JTextField codeField;
    private JTextField nameField;
    private JTextField typeField;
    private JComboBox<Room> roomComboBox;
    private JTextField valueField;
    private boolean confirmed;
    
    public AssetDialog(Window owner, Asset asset) {
        super(owner, asset == null ? "Thêm Tài Sản" : "Sửa Tài Sản", ModalityType.APPLICATION_MODAL);
        this.confirmed = false;
        this.assetService = new AssetService();
        this.roomService = new RoomService();
        this.asset = asset;
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mã:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        codeField = new JTextField(20);
        formPanel.add(codeField, gbc);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Loại:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        typeField = new JTextField(20);
        formPanel.add(typeField, gbc);
        
        // Room
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Phòng:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roomComboBox = new JComboBox<>();
        roomComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Room) {
                    Room room = (Room) value;
                    value = room.getCode() + " - " + room.getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        formPanel.add(roomComboBox, gbc);
        
        // Value
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Giá trị:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        valueField = new JTextField(20);
        formPanel.add(valueField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load rooms
        loadRooms();
    }
    
    private void loadRooms() {
        try {
            log.info("Đang tải danh sách phòng");
            List<Room> rooms = roomService.getAllRooms();
            roomComboBox.removeAllItems();
            for (Room room : rooms) {
                roomComboBox.addItem(room);
            }
            log.info("Đã tải {} phòng", rooms.size());
        } catch (Exception e) {
            log.error("Lỗi tải danh sách phòng: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tải danh sách phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadData() {
        if (asset != null) {
            log.info("Đang tải dữ liệu tài sản: {}", asset);
            codeField.setText(asset.getCode());
            nameField.setText(asset.getName());
            typeField.setText(asset.getType());
            valueField.setText(asset.getValue().toString());
            
            // Set selected room
            for (int i = 0; i < roomComboBox.getItemCount(); i++) {
                Room room = roomComboBox.getItemAt(i);
                if (room.getId().equals(asset.getRoomId())) {
                    roomComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Disable code field in edit mode
            codeField.setEnabled(false);
            log.info("Đã tải dữ liệu tài sản thành công");
        }
    }
    
    private void save() {
        try {
            log.info("Đang lưu tài sản...");
            Asset assetData = new Asset();
            if (asset != null) {
                assetData.setId(asset.getId());
            }
            assetData.setCode(codeField.getText().trim());
            assetData.setName(nameField.getText().trim());
            assetData.setType(typeField.getText().trim());
            
            Room selectedRoom = (Room) roomComboBox.getSelectedItem();
            if (selectedRoom != null) {
                assetData.setRoomId(selectedRoom.getId());
            }
            
            try {
                assetData.setValue(new BigDecimal(valueField.getText().trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá trị không hợp lệ");
            }
            
            if (asset == null) {
                log.info("Tạo tài sản mới: {}", assetData);
                assetService.createAsset(assetData);
                log.info("Đã tạo tài sản thành công");
            } else {
                log.info("Cập nhật tài sản: {}", assetData);
                assetService.updateAsset(assetData);
                log.info("Đã cập nhật tài sản thành công");
            }
            
            confirmed = true;
            dispose();
        } catch (Exception e) {
            log.error("Lỗi lưu tài sản: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi lưu tài sản: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}