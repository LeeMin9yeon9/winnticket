package kr.co.winnticket.channels.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.channels.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.dto.ChannelListGetResDto;
import kr.co.winnticket.channels.service.ChannelService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채널", description = "채널 관리 > 채널 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService service;
    @GetMapping
    @Operation(summary = "채널 목록 조회", description = "채널 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChannelListGetResDto>>>getChannelList(
            @Parameter(description = "채널코드")
            @RequestParam(required = false) String code,
            @Parameter(description = "채널이름")
            @RequestParam(required = false) String name,
            @Parameter(description = "회사이름")
            @RequestParam(required = false) String companyName
    ){

        List<ChannelListGetResDto> list = service.getChannelList(code,name,companyName);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @Operation(summary = "채널 등록", description = "채널을 등록합니다.")
    public ResponseEntity<ApiResponse<Void>>postChannel(
            @RequestBody ChannelCreateReqDto model
    ){
        service.createChannel(model);
        return ResponseEntity.ok(ApiResponse.success("채널이 등록되었습니다",null));
    }


}
