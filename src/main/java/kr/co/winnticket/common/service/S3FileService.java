package kr.co.winnticket.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cdn.url}")
    private String cdnUrl;

    public List<String> uploadFiles(MultipartFile[] files) {

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {

            String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                amazonS3.putObject(bucket, key, file.getInputStream(), metadata);

                urls.add(cdnUrl + "/" + key);

            } catch (Exception e) {
                throw new RuntimeException("S3 업로드 실패", e);
            }
        }

        return urls;
    }

    public void deleteFiles(List<String> imageUrls) {

        for (String url : imageUrls) {

            String key = url.replace(cdnUrl + "/", "");
            amazonS3.deleteObject(bucket, key);
        }
    }
}
