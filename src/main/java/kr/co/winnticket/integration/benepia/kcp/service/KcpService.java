package kr.co.winnticket.integration.benepia.kcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.kcp.dto.*;
import kr.co.winnticket.integration.benepia.kcp.util.KcpCertUtil;
import kr.co.winnticket.integration.benepia.kcp.util.KcpKeyUtil;
import kr.co.winnticket.integration.benepia.kcp.util.KcpSignUtil;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class KcpService {

    private final BenepiaProperties properties;
    private final ObjectMapper objectMapper;
    private final OrderShopMapper orderShopMapper;

    // KCP 포인트 조회
    public KcpPointResDto getPoint(KcpPointReqDto dto) {
        try {
            String certInfo = KcpCertUtil.loadCert(properties.getKcp().getCertPath());

            String url = properties.getKcp().getBaseUrl() + "/gw/hub/v1/payment";

            Map<String, Object> body = new HashMap<>();
            body.put("site_cd", properties.getKcp().getSiteCd());
            body.put("kcp_cert_info", certInfo);
            body.put("pay_method", "POINT");
            body.put("ordr_idxx", dto.getOrderNo());
            body.put("amount", dto.getAmount());

            body.put("pt_issue", "SCWB");
            body.put("pt_txtype", "97000000");
            body.put("pt_idno", dto.getBenepiaId());
            body.put("pt_pwd", dto.getBenepiaPwd());
            body.put("pt_memcorp_cd", properties.getCustCoCd());
            //body.put("pt_memcorp_cd",dto.getMemcorpCd());

            String json = objectMapper.writeValueAsString(body);

            log.info("===== KCP REQUEST START =====");
            log.info("baseUrl = {}", properties.getKcp().getBaseUrl());
            log.info("KCP URL = {}", url);
            log.info("certPath = {}", properties.getKcp().getCertPath());
            log.info("REQUEST BODY = {}", json);
            log.info("===== KCP REQUEST END =====");


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.info("[KCP POINT RES] {}", response.body());

            return objectMapper.readValue(
                    response.body(),
                    KcpPointResDto.class
            );

        } catch (Exception e) {
            log.error("===== KCP POINT ERROR START =====");

            e.printStackTrace();

            log.error("===== KCP POINT ERROR END =====");

            throw new RuntimeException("KCP 포인트 조회 실패", e);
        }
    }

    // KCP 포인트 결제
    public KcpPointPayResDto pointPay(KcpPointPayReqDto dto) {
        try {
            String certInfo = Files.readString(Paths.get(properties.getKcp().getCertPath()));

            String url = properties.getKcp().getBaseUrl() + "/gw/hub/v1/payment";

            Map<String, Object> body = new HashMap<>();
            body.put("site_cd", properties.getKcp().getSiteCd());    // 사이트코드
            body.put("kcp_cert_info", certInfo);                         // 서비스인증서
            body.put("pay_method", "POINT");                         // 결제수단
            body.put("ordr_idxx", dto.getOrderNo());                // 주문번호
            body.put("amount", dto.getAmount());                     // 결제금액

            body.put("good_name",dto.getProductName());             // 상품명
            body.put("good_cd",dto.getProductCode());               // 상품코드
            body.put("buyr_name",dto.getBuyerName());               // 주문자명
            body.put("buyr_mail",dto.getBuyerEmail());              // 주문자 이메일
            body.put("buyr_tel2",dto.getBuyerPhone());              // 주문자 연락처

            body.put("pt_issue", "SCWB");                           // 포인트기관 : SCWB
            body.put("pt_txtype", "91200000");                      // 포인트전문유형 : 91200000

            body.put("pt_idno", dto.getBenepiaId());                // 베네피아ID
            body.put("pt_pwd", dto.getBenepiaPwd());                // 베네피아PW

            body.put("pt_memcorp_cd", properties.getCustCoCd());    // 소속사코드 : z819
            body.put("pt_mny",String.valueOf(dto.getAmount()));     // 포인트결제금액 : amount 금액과 동일해야함
            body.put("pt_paycode", "04");                           // 결제코드:04

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(request,
                                    HttpResponse.BodyHandlers.ofString());

            log.info("[KCP POINTPAY RES] {}", response.body());

            return objectMapper.readValue(
                    response.body(),
                    KcpPointPayResDto.class
            );
        } catch (Exception e) {
            throw new RuntimeException("KCP 포인트 결제 실패",e);
        }

    }

    // 포인트 결제 성공 시 상태 업데이트
    @Transactional
    public KcpPointPayResDto pointPayAndUpdate(KcpPointPayReqDto dto) {

        KcpPointPayResDto res = pointPay(dto);  // 기존 KCP 호출

        if (res == null || !"0000".equals(res.getRes_cd())) {

            orderShopMapper.updatePaymentFailed(dto.getOrderNo());

            throw new IllegalStateException(
                    "포인트 결제 실패: " + (res != null ? res.getRes_msg() : "응답없음")
            );
        }

        // 결제 성공 → 주문 상태 PAID 처리
        orderShopMapper.updatePointPaymentApproved(
                dto.getOrderNo(),
                res.getTno(),
                res.getApp_no()
        );

        return res;
    }

    // 포인트 취소
    public KcpModResDto cancelPoint(KcpPointCancelReqDto dto){
        try{
            String siteCd = properties.getKcp().getSiteCd();

            // 서비스 인증서 로딩
            String cert = KcpCertUtil.loadCert(properties.getKcp().getCertPath());


            // 개인키 로딩
            PrivateKey privateKey = KcpKeyUtil.loadPrivateKey(
                    properties.getKcp().getPrivateKeyPath(),
                    properties.getKcp().getPrivateKeyPassword()
            );


            // 전자서명 생성
            String signData = KcpSignUtil.makeSignature(
                    siteCd,
                    dto.getTno(),
                    "STSC",
                    privateKey
            );
            String url = properties.getKcp().getBaseUrl()
                    + "/gw/mod/v1/cancel";

            Map<String, Object> body = new HashMap<>();
            body.put("site_cd", siteCd);                // 사이트코드
            body.put("kcp_cert_info", cert);            // 서비스 인증서
            body.put("tno", dto.getTno());              // KCP 거래번호
            body.put("mod_type", "STSC");              // 취소 요청 구분
            body.put("kcp_sign_data", signData);        // 서명 데이터
            body.put("mod_desc", dto.getCancelReason());    // 취소 사유

            String json = objectMapper.writeValueAsString(body);

            log.info("[KCP CANCEL REQ] {}", json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(request,
                                    HttpResponse.BodyHandlers.ofString());

            log.info("[KCP CANCEL RES] {}", response.body());

            KcpModResDto res =
                    objectMapper.readValue(
                            response.body(),
                            KcpModResDto.class
                    );

            if (!"0000".equals(res.getRes_cd())) {
                throw new IllegalStateException(
                        "KCP 취소 실패: " + res.getRes_msg()
                );
            }

            return res;

        } catch (Exception e) {
            throw new RuntimeException("KCP 포인트 취소 실패", e);
        }

        }
    }
