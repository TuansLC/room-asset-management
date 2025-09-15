package com.ptit.ltm.client;

import com.formdev.flatlaf.FlatLightLaf;
import com.ptit.ltm.client.ui.asset.AssetPanel;
import com.ptit.ltm.client.ui.room.RoomPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class RoomAssetManagementClient extends JFrame {
    
    public RoomAssetManagementClient() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Hệ thống Quản lý Tài sản Phòng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Quản lý Phòng", new RoomPanel());
        tabbedPane.addTab("Quản lý Tài sản", new AssetPanel());
        
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // Add status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusBar.add(statusLabel);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            SwingUtilities.invokeLater(() -> {
                RoomAssetManagementClient frame = new RoomAssetManagementClient();
                frame.setVisible(true);
            });
        } catch (UnsupportedLookAndFeelException e) {
            log.error("Lỗi khởi động ứng dụng: {}", e.getMessage());
        }
    }
}