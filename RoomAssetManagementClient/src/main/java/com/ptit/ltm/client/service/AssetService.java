package com.ptit.ltm.client.service;

import com.ptit.ltm.common.model.Asset;
import com.ptit.ltm.common.model.Request;
import com.ptit.ltm.common.model.RequestType;
import com.ptit.ltm.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class AssetService {
    private final TCPClient tcpClient;
    
    public AssetService() {
        this.tcpClient = TCPClient.getInstance();
    }
    
    public Asset createAsset(Asset asset) throws Exception {
        log.info("Gửi yêu cầu tạo tài sản: {}", asset);
        Request request = new Request(RequestType.ADD_ASSET, asset);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tạo tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        Asset createdAsset = (Asset) response.getData();
        log.info("Đã tạo tài sản thành công: {}", createdAsset);
        return createdAsset;
    }
    
    public Asset updateAsset(Asset asset) throws Exception {
        log.info("Gửi yêu cầu cập nhật tài sản: {}", asset);
        Request request = new Request(RequestType.UPDATE_ASSET, asset);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi cập nhật tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        Asset updatedAsset = (Asset) response.getData();
        log.info("Đã cập nhật tài sản thành công: {}", updatedAsset);
        return updatedAsset;
    }
    
    public void deleteAsset(Integer id) throws Exception {
        log.info("Gửi yêu cầu xóa tài sản với ID: {}", id);
        Request request = new Request(RequestType.DELETE_ASSET, id);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi xóa tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        log.info("Đã xóa tài sản thành công");
    }
    
    public Asset getAssetById(Integer id) throws Exception {
        log.info("Tìm tài sản theo ID: {}", id);
        Request request = new Request(RequestType.SEARCH_ASSET, id.toString());
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tìm tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        if (response.getData() == null) {
            log.error("Không có dữ liệu trả về cho ID: {}", id);
            return null;
        }

        List<Asset> assets = (List<Asset>) response.getData();
        log.info("Dữ liệu trả về: {}", assets);
        
        if (assets.isEmpty()) {
            log.error("Không tìm thấy tài sản với ID: {}", id);
            return null;
        }

        Asset asset = assets.get(0);
        log.info("Đã tìm thấy tài sản: {}", asset);
        return asset;
    }
    
    @SuppressWarnings("unchecked")
    public List<Asset> getAllAssets() throws Exception {
        log.info("Lấy danh sách tất cả tài sản");
        Request request = new Request(RequestType.SEARCH_ASSET, "");
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi lấy danh sách tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        List<Asset> assets = (List<Asset>) response.getData();
        log.info("Đã lấy được {} tài sản", assets.size());
        return assets;
    }
    
    @SuppressWarnings("unchecked")
    public List<Asset> searchAssets(String query) throws Exception {
        log.info("Tìm kiếm tài sản với từ khóa: {}", query);
        Request request = new Request(RequestType.SEARCH_ASSET, query);
        Response response = tcpClient.sendRequest(request);
        
        if (!response.isSuccess()) {
            log.error("Lỗi tìm kiếm tài sản: {}", response.getMessage());
            throw new Exception(response.getMessage());
        }
        
        List<Asset> assets = (List<Asset>) response.getData();
        log.info("Tìm thấy {} tài sản", assets.size());
        return assets;
    }
}