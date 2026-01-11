package kr.co.winnticket.channels.channelProducts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.channels.channelProducts.dto.ChannelProductListResDto;
import kr.co.winnticket.channels.channelProducts.service.ChannelProductService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Service
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/channels/{channelId}/products")
@Tag(name = "채널", description = "채널 관리 > 채널 상세 > 상품 관리")
public class ChannelProductController {

    private final ChannelProductService service;
    @GetMapping
    @Operation(summary = "채널 상품 목록 조회 (검색 포함)")
    public ResponseEntity<ApiResponse<List<ChannelProductListResDto>>> getChannelProducts(
            @Parameter(description = "채널ID")
            @PathVariable UUID channelId,
            @Parameter(description = "검색(상품코드 , 이름)")
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean exclusion
    ) {
        List<ChannelProductListResDto> list = service.getChannelProducts(channelId,keyword,exclusion);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{productId}/exclude")
    @Operation(summary = "채널 상품 제외")
    public ResponseEntity<ApiResponse<Void>> excludeProduct(
            @PathVariable UUID channelId,
            @PathVariable UUID productId
            ){
        service.excludeProduct(channelId , productId);
        return ResponseEntity.ok(ApiResponse.success("상품이 채널에서 제외되었습니다.",null));
    }

    @DeleteMapping("/{productId}/include")
    @Operation(summary = "채널 상품 복구")
    public ResponseEntity<ApiResponse<Void>> includeProduct(
            @PathVariable UUID channelId,
            @PathVariable UUID productId
    ){
        service.includeProduct(channelId,productId);
        return ResponseEntity.ok(ApiResponse.success("상품이 채널에 다시 추가되었습니다.",null));
    }


}
