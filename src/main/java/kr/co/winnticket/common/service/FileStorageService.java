package kr.co.winnticket.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    List<String> uploadFiles(MultipartFile[] files);

    List<String> deleteFiles(List<String> imageUrls);
}
