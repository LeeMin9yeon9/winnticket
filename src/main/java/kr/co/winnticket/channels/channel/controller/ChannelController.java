package kr.co.winnticket.channels.channel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.channels.channel.dto.ChannelInfoResGetDto;
import kr.co.winnticket.channels.channel.service.ChannelService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "채널(주문용)", description = "채널 관리 > 채널 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService service;

    @GetMapping("/{id}")
    @Operation(summary = "(주문용) 채널 기본 정보", description = "주문 시 채널의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChannelInfoResGetDto>> getChannelInfo(
            @Parameter(description = "채널_ID")
            @PathVariable UUID id
    ){
        ChannelInfoResGetDto info = service.selectChannel(id);
        return ResponseEntity.ok(ApiResponse.success(info));
    }


}
