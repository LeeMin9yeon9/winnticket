package kr.co.winnticket.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 단건 파일 업로드
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 저장 폴더 생성
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        file.transferTo(filePath.toFile());

        return fileName; // 파일명만 반환
    }

    /**
     * 다건 파일 업로드
     */
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> savedFiles = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return savedFiles;
        }

        for (MultipartFile file : files) {
            String fileName = uploadFile(file); // 단건 메서드 재사용
            if (fileName != null) {
                savedFiles.add(fileName);
            }
        }

        return savedFiles;
    }

    /**
     * 단건 파일 삭제
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + fileName, e);
        }
    }

    /**
     * 다건 파일 삭제
     */
    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (String fileName : fileNames) {
            deleteFile(fileName);
        }
    }
}
