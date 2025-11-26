package kr.co.winnticket.auth.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResDto<T> {

    @Schema(description = "성공 여부" , example = "true")
    private boolean success;

    @Schema(description = "메세지" , example = "로그인 성공")
    private String message;

    @Schema(description = "실제 데이터")
    private T date;

    public static <T> ApiResDto<T> success(String message, T data){
        return ApiResDto.<T>builder()
                .success(true)
                .message(message)
                .date(data)
                .build();
    }

    public static <T> ApiResDto<T> fail(String message){
        return ApiResDto.<T>builder()
                .success(false)
                .message(message)
                .date(null)
                .build();
    }

}
