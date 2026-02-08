package kr.co.winnticket.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3FileService implements FileStorageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cdn.url}")
    private String cdnUrl;

    private static final List<String> ALLOWED_EXT =
            List.of("jpg", "jpeg", "png", "gif", "webp");


    @Override
    public List<String> uploadFiles(MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {

            validateFile(file);

            String originalName = Paths.get(file.getOriginalFilename())
                    .getFileName().toString();

            String extension = getExtension(originalName);

            String key = "uploads/" + UUID.randomUUID() + "." + extension;

            try (InputStream inputStream = file.getInputStream()) {

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType("image/" + extension);

                amazonS3.putObject(bucket, key, inputStream, metadata);

                urls.add(cdnUrl + "/" + key);

            } catch (Exception e) {
                throw new RuntimeException("S3 업로드 실패: " + originalName, e);
            }
        }

        return urls;
    }


    @Override
    public List<String> deleteFiles(List<String> imageUrls) {

        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }

        List<String> deletedKeys = new ArrayList<>();

        for (String url : imageUrls) {

            String key = extractKeyFromUrl(url);

            amazonS3.deleteObject(bucket, key);
            deletedKeys.add(key);
        }

        return deletedKeys;
    }


    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

//        if (file.getSize() > MAX_FILE_SIZE) {
//            throw new IllegalArgumentException("파일 크기 초과 (5MB)");
//        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String ext = getExtension(originalName);

        if (!ALLOWED_EXT.contains(ext.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않은 확장자입니다.");
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("확장자가 없습니다.");
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf("uploads/"));
    }
}
