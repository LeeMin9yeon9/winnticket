package kr.co.winnticket.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileStorageService {
    List<String> uploadFiles(MultipartFile[] files);

    List<String> deleteFiles(List<String> imageUrls);
}
