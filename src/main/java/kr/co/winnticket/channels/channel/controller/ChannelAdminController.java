package kr.co.winnticket.channels.channel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.channels.channel.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.channel.dto.ChannelInfoResGetDto;
import kr.co.winnticket.channels.channel.dto.ChannelListGetResDto;
import kr.co.winnticket.channels.channel.dto.ChannelPatchReqDto;
import kr.co.winnticket.channels.channel.service.ChannelService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "채널", description = "채널 관리 > 채널 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/channels")
public class ChannelAdminController {
    private final ChannelService service;
    @GetMapping
    @Operation(summary = "채널 목록 조회", description = "채널 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChannelListGetResDto>>> getChannelList(
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

    @GetMapping("/{id}")
    @Operation(summary = "채널 기본 정보", description = "채널의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChannelInfoResGetDto>> getChannelInfo(
            @Parameter(description = "채널_ID")
            @PathVariable UUID id
    ){
        ChannelInfoResGetDto info = service.selectChannel(id);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @PatchMapping("/{id}/info")
    @Operation(summary = "채널 정보 수정" ,description = "채널의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> patchChannel(
            @Parameter(description = "채널_ID")
            @PathVariable UUID id,
            @RequestBody ChannelPatchReqDto model
    )throws Exception{
        service.updateChannel(id,model);
        return ResponseEntity.ok(ApiResponse.success("채널이 수정되었습니다.",null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "채널 삭제" ,description = "채널을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteChannel(
            @Parameter(description = "채널_ID")
            @PathVariable UUID id
    ){
        service.deleteChannel(id);
        return ResponseEntity.ok(ApiResponse.success("채널이 삭제되었습니다.",null));
    }

    @PatchMapping("visible/{id}")
    @Operation(summary = "채널 활성/비활성 " , description = "채널 활/비활성화를 할 수있습니다.")
    public ResponseEntity<ApiResponse<Void>> visibleChannel(
            @PathVariable UUID id,
            @PathVariable Boolean visible
    ) throws NotFoundException {
        service.visibleChannel(id,visible);
        return ResponseEntity.ok(ApiResponse.success("채널 활성/비할성되었습니다.",null));
    }


}

