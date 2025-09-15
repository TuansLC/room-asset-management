package com.ptit.ltm.client.ui.room;

import com.ptit.ltm.client.service.RoomService;
import com.ptit.ltm.common.model.Room;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class RoomDialog extends JDialog {
    private final RoomService roomService;
    private final Room room;
    private boolean confirmed;
    
    private JTextField codeField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    
    public RoomDialog(Window owner, Room room) {
        super(owner, room == null ? "Thêm Phòng" : "Sửa Phòng", ModalityType.APPLICATION_MODAL);
        this.confirmed = false;
        this.roomService = new RoomService();
        this.room = room;
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
        formPanel.add(new JLabel("Mã phòng:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        codeField = new JTextField(20);
        formPanel.add(codeField, gbc);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên phòng:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
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
    }
    
    private void loadData() {
        if (room != null) {
            log.info("Đang tải dữ liệu phòng: {}", room);
            codeField.setText(room.getCode());
            nameField.setText(room.getName());
            descriptionArea.setText(room.getDescription());
            
            // Disable code field in edit mode
            codeField.setEnabled(false);
            log.info("Đã tải dữ liệu phòng thành công");
        }
    }
    
    private void save() {
        try {
            log.info("Đang lưu thông tin phòng...");
            Room roomData = new Room();
            if (room != null) {
                roomData.setId(room.getId());
            }
            roomData.setCode(codeField.getText().trim());
            roomData.setName(nameField.getText().trim());
            roomData.setDescription(descriptionArea.getText().trim());
            
            if (room == null) {
                log.info("Tạo phòng mới: {}", roomData);
                roomService.createRoom(roomData);
                log.info("Đã tạo phòng thành công");
            } else {
                log.info("Cập nhật phòng: {}", roomData);
                roomService.updateRoom(roomData);
                log.info("Đã cập nhật phòng thành công");
            }
            
            confirmed = true;
            dispose();
        } catch (Exception e) {
            log.error("Lỗi lưu phòng: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi lưu phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}