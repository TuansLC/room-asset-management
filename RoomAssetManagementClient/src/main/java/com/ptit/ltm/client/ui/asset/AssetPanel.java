package com.ptit.ltm.client.ui.asset;

import com.ptit.ltm.client.service.AssetService;
import com.ptit.ltm.common.model.Asset;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class AssetPanel extends JPanel {
    private final AssetService assetService;
    private JTable assetTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public AssetPanel() {
        this.assetService = new AssetService();
        initComponents();
        loadAssets();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> searchAssets());
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table
        String[] columns = {"ID", "Mã tài sản", "Tên tài sản", "Loại", "Mã phòng", "Giá trị"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return BigDecimal.class;
                return Object.class;
            }
        };
        assetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assetTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm tài sản");
        JButton editButton = new JButton("Sửa tài sản");
        JButton deleteButton = new JButton("Xóa tài sản");
        
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteAsset());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadAssets() {
        try {
            log.info("Đang tải danh sách tài sản");
            List<Asset> assets = assetService.getAllAssets();
            updateTable(assets);
            log.info("Đã tải {} tài sản", assets.size());
        } catch (Exception e) {
            log.error("Lỗi tải danh sách tài sản: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tải danh sách tài sản: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Asset> assets) {
        tableModel.setRowCount(0);
        for (Asset asset : assets) {
            tableModel.addRow(new Object[]{
                asset.getId(),
                asset.getCode(),
                asset.getName(),
                asset.getType(),
                asset.getRoomId(),
                asset.getValue()
            });
        }
    }
    
    private void searchAssets() {
        try {
            String query = searchField.getText().trim();
            log.info("Tìm kiếm tài sản với từ khóa: {}", query);
            List<Asset> assets = assetService.searchAssets(query);
            updateTable(assets);
            log.info("Tìm thấy {} tài sản", assets.size());
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm tài sản: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tìm kiếm tài sản: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        AssetDialog dialog = new AssetDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadAssets();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn tài sản cần sửa",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            log.info("Đang tải thông tin tài sản có ID: {}", id);
            
            Asset asset = assetService.getAssetById(id);
            if (asset == null) {
                log.error("Không tìm thấy tài sản có ID: {}", id);
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin tài sản",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log.info("Mở form sửa tài sản: {}", asset);
            AssetDialog dialog = new AssetDialog(SwingUtilities.getWindowAncestor(this), asset);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                log.info("Đã cập nhật tài sản, tải lại dữ liệu");
                loadAssets();
            }
        } catch (Exception e) {
            log.error("Lỗi tải thông tin tài sản: {}", e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi tải thông tin tài sản: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn tài sản cần xóa",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String code = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa tài sản " + code + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                log.info("Xóa tài sản có ID: {}", id);
                assetService.deleteAsset(id);
                log.info("Đã xóa tài sản thành công");
                loadAssets();
            } catch (Exception e) {
                log.error("Lỗi xóa tài sản: {}", e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Lỗi xóa tài sản: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}