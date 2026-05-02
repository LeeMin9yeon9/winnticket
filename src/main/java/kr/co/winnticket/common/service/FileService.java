package kr.co.winnticket.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("!prod")
public class FileService implements FileStorageService  {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.url}")
    private String serverUrl;

    private static final List<String> ALLOWED_EXT =
            List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB

    // 업로드 처리
    public List<String> uploadFiles(MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 폴더 없으면 생성
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFile(file);

            String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            // 디렉토리 traversal 방지: 파일명만 사용
            String safeName = Paths.get(original).getFileName().toString();
            String savedName = UUID.randomUUID() + "_" + safeName;
            Path savePath = Paths.get(uploadDir, savedName);

            try {
                Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장에 실패했습니다.");
            }

            urls.add(serverUrl + "/uploads/" + savedName);
        }

        return urls;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기 초과 (최대 10MB)");
        }
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            throw new IllegalArgumentException("확장자가 없습니다.");
        }
        String ext = name.substring(dot + 1).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않은 확장자입니다.");
        }
    }

    public List<String> deleteFiles(List<String> imageUrls) {
        List<String> deletedFiles = new ArrayList<>();

        for (String url : imageUrls) {
            // URL → 파일명 추출
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, fileName);

            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    deletedFiles.add(fileName);
                }
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제에 실패했습니다: " + fileName);
            }
        }

        if (deletedFiles.isEmpty()) {
            throw new RuntimeException("삭제 가능한 파일이 없습니다.");
        }

        return deletedFiles;
    }
}
