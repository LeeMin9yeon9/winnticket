package kr.co.winnticket.cart.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.winnticket.cart.dto.responseDto.ShopCartAddReqDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartCountResDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartResDto;
import kr.co.winnticket.cart.service.ShopCartService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "쇼핑몰 장바구니")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shopCart")
public class ShopCartController {

    private final ShopCartService service;

    @GetMapping
    @Operation(summary = "장바구니 조회")
    public ResponseEntity<ApiResponse<ShopCartResDto>> getCart(HttpSession session){
        System.out.println("SESSION CART = " + session.getAttribute("SHOP_CART"));
        return ResponseEntity.ok(
                ApiResponse.success(service.getCartView(session))
        );
    }

    @PostMapping
    @Operation(summary = "장바구니 추가")
    public ResponseEntity<ApiResponse<Void>> addCart(
            @RequestBody
            @Valid
            ShopCartAddReqDto model,
            HttpSession session
    ){
        service.addCart(session,model);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    @Operation(summary = "상품 수량 변경")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable UUID id,
            @RequestParam int quantity,
            HttpSession session
    ){
        service.updateQuantity(session,id,quantity);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/count")
    @Operation(summary = "장바구트 수량 카운트")
    public ResponseEntity<ApiResponse<ShopCartCountResDto>> getCartCount(HttpSession session
    ) {
        int count = service.getCartCount(session);

        ShopCartCountResDto res = new ShopCartCountResDto();
        res.setCount(count);

        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "장바구니 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteCart(
            @PathVariable UUID id,
            HttpSession session
    ){
        service.deleteItem(session,id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/session")
    @Operation(summary = "장바구니 세션 강제 삭제")
    public void clearCart(HttpSession session) {
        session.removeAttribute("SHOP_CART");
    }





}
