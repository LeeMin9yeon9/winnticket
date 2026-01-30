package kr.co.winnticket.common.controller;

import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/common/files")
public class FileController {
    private final FileService fileService;

    // 이미지 업로드
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @RequestPart(value="files", required=false) MultipartFile[] files
    ) {
        // 파일 여부 체크
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("업로드할 파일이 없습니다.", "NO_FILES")
            );
        }

        List<String> urls = fileService.uploadFiles(files);

        return ResponseEntity.ok(
                ApiResponse.success("이미지가 업로드되었습니다.", urls)
        );
    }

    // 이미지 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<List<String>>> deleteImages(
            @RequestParam(value="url", required=false) List<String> fileUrls
    ) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("삭제할 파일이 없습니다.", "NO_DELETE_TARGET")
            );
        }

        List<String> deletedFiles = fileService.deleteFiles(fileUrls);

        return ResponseEntity.ok(
                ApiResponse.success("이미지가 삭제되었습니다.", deletedFiles)
        );
    }
}
