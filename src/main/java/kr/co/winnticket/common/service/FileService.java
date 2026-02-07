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
@Profile("dev")
public class FileService implements FileStorageService  {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.url}")
    private String serverUrl;

    // 업로드 처리
    public List<String> uploadFiles(MultipartFile[] files) {

        // 폴더 없으면 생성
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String savedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadDir, savedName);  // 안전한 경로 결합

            try {
                Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장에 실패했습니다.");
            }

            urls.add(serverUrl + "/uploads/" + savedName);
        }

        return urls;
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
