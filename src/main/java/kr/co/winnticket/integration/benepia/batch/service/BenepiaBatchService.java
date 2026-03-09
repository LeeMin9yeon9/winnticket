package kr.co.winnticket.integration.benepia.batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.batch.dto.BenepiaResponse;
import kr.co.winnticket.integration.benepia.batch.mapper.BenepiaBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BenepiaBatchService {

    private final BenepiaBatchMapper mapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${benepia.batch-base-url}")
    private String batchBaseUrl;

    @Value("${benepia.kcp-co-cd}")
    private String kcpCoCd;

    @Value("${benepia.cust-co-cd}")
    private String custCoCd;

    @Value("${benepia-batch.max-file-bytes:20971520}")
    private long maxFileBytes;

    @Value("${benepia-batch.retry-count:3}")
    private int retryCount;

    @Value("${benepia-batch.retry-sleep-ms:2000}")
    private long retrySleepMs;

    @Value("${benepia-batch.work-dir:/home/ubuntu/benepia}")
    private String workDir;

    @Value("${benepia.api-key}")
    private String apiKey;

    /** 외부에서 호출하는 진입점(컨트롤러/스케줄러 공용) */
    public void executeTicketBatch() throws Exception {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        ensureWorkDir();

        // 1) IDS
        try {
            List<Map<String, Object>> ids = mapper.selectBenepiaIds();
            List<File> idFiles = createIdsFilesSplit(ids, today);
            for (File f : idFiles) {
                uploadWithRetry(f, "ids", "IDS_UPLOAD", today);
                safeDelete(f);
            }
            log(today, "IDS", "SUCCESS", null, "ids 업로드 완료");
        } catch (Exception e) {
            log(today, "IDS", "FAIL", null, shortMsg(e));
            throw e;
        }

        // 2) PRODUCTS (티켓)
        String productsBaseName; // done에 들어갈 "일련번호 제거" baseName
        try {
            List<Map<String, Object>> rows = mapper.selectBenepiaProducts();
            List<File> productFiles = createTicketProductsFilesSplit(rows, today);

            // done 파일명 base: {kcp}_{prdGb}_products_{date}
            productsBaseName = kcpCoCd + "_ticket_products_" + today;

            for (File f : productFiles) {
                uploadWithRetry(f, "products", "PRODUCTS_UPLOAD", today);
                safeDelete(f);
            }
            log(today, "PRODUCTS", "SUCCESS", null, "products 업로드 완료");
        } catch (Exception e) {
            log(today, "PRODUCTS", "FAIL", null, shortMsg(e));
            throw e;
        }

        // 3) DONE
        try {
            doneWithRetry(productsBaseName, today);
            log(today, "DONE", "SUCCESS", productsBaseName, "done 호출 완료");
        } catch (Exception e) {
            log(today, "DONE", "FAIL", productsBaseName, shortMsg(e));
            throw e;
        }
    }

    /* -------------------------
       파일 생성: IDS (20MB 분할)
     ------------------------- */
    private List<File> createIdsFilesSplit(List<Map<String, Object>> ids, String date) throws Exception {
        List<Map<String, String>> items = new ArrayList<>();
        for (Map<String, Object> row : ids) {
            Map<String, String> m = new HashMap<>();
            m.put("coopCoCd", custCoCd);
            m.put("prdId", String.valueOf(row.get("prdId")));
            items.add(m);
        }

        return splitBySizeAndWriteJsonArray(
                items,
                "productIds",
                (seq) -> kcpCoCd + "_ticket_ids_" + date + "_" + seq + ".json"
        );
    }

    /* -------------------------
       파일 생성: PRODUCTS 티켓 (20MB 분할)
     ------------------------- */
    private List<File> createTicketProductsFilesSplit(List<Map<String, Object>> rows, String date) throws Exception {
        List<Map<String, Object>> wrappers = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> wrapper = new LinkedHashMap<>();

            // product (필수 필드 구성)
            Map<String, Object> product = new LinkedHashMap<>();
            product.put("coopCoCd", custCoCd);
            product.put("prdId", row.get("prdId"));
            product.put("prdNm", row.get("prdNm"));
            product.put("orgnPrc", toInt(row.get("orgnPrc")));
            product.put("salePrc", toInt(row.get("salePrc")));
            product.put("prdImgUrl", row.get("prdImgUrl"));
            product.put("prdDtlUrlTyp", "L");
            product.put("prdDtlUrl", row.get("prdDtlUrl"));
            product.put("prdMobDtlUrlTyp", "L");
            product.put("prdMobDtlUrl", row.get("prdMobDtlUrl"));
            product.put("keyword", "");
            product.put("prdType", "10"); // 여행상품(국내) :contentReference[oaicite:4]{index=4}
            product.put("prdSubTitle", "");
            product.put("prdDesc", nz(row.get("prdDesc")));
            product.put("regDate", nz(row.get("regDate"))); // 이미 yyyyMMddHHmmss로 SQL에서 만들었음
            product.put("updDate", nz(row.get("updDate")));
            product.put("param1", "");
            product.put("param2", "");
            product.put("param3", "");

            // travel (티켓이면 prdGb=03)
            Map<String, Object> travel = new LinkedHashMap<>();
            travel.put("prdGb", "03"); // 티켓 :contentReference[oaicite:5]{index=5}
            travel.put("nationalCd", "KR");
            travel.put("regionCd", nz(row.get("regionCd")));
            travel.put("districCd", "");
            travel.put("telNo", "");
            travel.put("zipCd", "");
            travel.put("addr1", "");
            travel.put("addr2", "");
            travel.put("homepage", "");
            travel.put("xPoint", 0);
            travel.put("yPoint", 0);

            // ticket
            Map<String, Object> ticket = new LinkedHashMap<>();
            ticket.put("ticketType", nz(row.get("ticketType"))); // 01~99 :contentReference[oaicite:6]{index=6}
            ticket.put("expireInfo", "");
            ticket.put("ticketPlace", String.valueOf(row.get("prdNm")));

            wrapper.put("product", product);
            wrapper.put("travel", travel);
            wrapper.put("ticket", ticket);

            // 문서 샘플처럼 다른 카테고리들은 null 처리 가능
            wrapper.put("lodge", null);
            wrapper.put("tour", null);
            wrapper.put("rentcar", null);

            wrappers.add(wrapper);
        }

        return splitBySizeAndWriteJsonArray(
                wrappers,
                "products",
                (seq) -> kcpCoCd + "_ticket_products_" + date + "_" + seq + ".json"
        );
    }

    /** 공통: rootKey 배열을 maxFileBytes에 맞춰 쪼개서 파일 생성 */
    private <T> List<File> splitBySizeAndWriteJsonArray(
            List<T> allItems,
            String rootKey,
            FileNameProvider fileNameProvider
    ) throws Exception {

        List<File> files = new ArrayList<>();
        int idx = 0;
        int seq = 0;

        while (idx < allItems.size()) {
            List<T> chunk = new ArrayList<>();

            // 최소 1개는 들어가야 하므로, 1개 넣고 검사
            chunk.add(allItems.get(idx));
            idx++;

            File file = writeJsonFile(rootKey, chunk, fileNameProvider.fileName(seq));
            while (idx < allItems.size() && file.length() <= maxFileBytes) {
                // 다음 1개 추가하고 다시 파일로 써서 사이즈 체크
                chunk.add(allItems.get(idx));
                File test = writeJsonFile(rootKey, chunk, fileNameProvider.fileName(seq));

                if (test.length() > maxFileBytes) {
                    // 방금 추가한 1개를 제거하고 확정 파일 다시 작성
                    chunk.remove(chunk.size() - 1);
                    file = writeJsonFile(rootKey, chunk, fileNameProvider.fileName(seq));
                    break;
                }

                file = test;
                idx++;
            }

            if (file.length() > maxFileBytes && chunk.size() == 1) {
                // 단일 아이템이 20MB 초과면 구조상 실패 (이미지/desc가 너무 큼)
                throw new IllegalStateException("단일 항목이 최대 파일 크기(20MB)를 초과합니다. rootKey=" + rootKey);
            }

            files.add(file);
            seq++;
        }

        return files;
    }

    private File writeJsonFile(String rootKey, List<?> items, String fileName) throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put(rootKey, items);

        Path path = Path.of(workDir, fileName);
        Files.createDirectories(path.getParent());

        File file = path.toFile();
        objectMapper.writeValue(file, root);
        return file;
    }

    /* -------------------------
       업로드 / 재시도
     ------------------------- */
    private void uploadWithRetry(File file, String typePath, String step, String batchDate) throws Exception {
        Exception last = null;

        for (int i = 1; i <= retryCount; i++) {
            try {
                BenepiaResponse res = uploadFile(file, typePath);
                if (res == null || !"0000".equals(res.getResCode())) {
                    throw new IllegalStateException("Benepia 응답 실패: " + (res == null ? "null" : res.getResCode() + " " + res.getResMsg()));
                }
                log(batchDate, step, "SUCCESS", file.getName(), "OK");
                return;
            } catch (Exception e) {
                last = e;
                log(batchDate, step, "FAIL", file.getName(), "try=" + i + " " + shortMsg(e));
                if (i < retryCount) Thread.sleep(retrySleepMs);
            }
        }

        throw last;
    }

    private BenepiaResponse uploadFile(File file, String typePath) {
        String url = batchBaseUrl + "/v1/partners/" + kcpCoCd + "/" + typePath;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<MultiValueMap<String, Object>> req = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<BenepiaResponse> res = restTemplate.postForEntity(url, req, BenepiaResponse.class);
            return res.getBody();
        } catch (RestClientResponseException e) {
            throw new RuntimeException("HTTP " + e.getRawStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    private void doneWithRetry(String baseFileName, String batchDate) throws Exception {
        Exception last = null;

        for (int i = 1; i <= retryCount; i++) {
            try {
                BenepiaResponse res = done(baseFileName);
                if (res == null || !"0000".equals(res.getResCode())) {
                    throw new IllegalStateException("Benepia DONE 실패: " + (res == null ? "null" : res.getResCode() + " " + res.getResMsg()));
                }
                return;
            } catch (Exception e) {
                last = e;
                log(batchDate, "DONE_CALL", "FAIL", baseFileName, "try=" + i + " " + shortMsg(e));
                if (i < retryCount) Thread.sleep(retrySleepMs);
            }
        }

        throw last;
    }

    private BenepiaResponse done(String baseFileName) {
        String url = batchBaseUrl + "/v1/partners/" + kcpCoCd + "/products/done/" + custCoCd;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("fileName", baseFileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<BenepiaResponse> res = restTemplate.postForEntity(url, req, BenepiaResponse.class);
            return res.getBody();
        } catch (RestClientResponseException e) {
            throw new RuntimeException("HTTP " + e.getRawStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    /* -------------------------
       유틸/로그/파일
     ------------------------- */
    private void ensureWorkDir() throws Exception {
        Files.createDirectories(Path.of(workDir));
    }

    private void safeDelete(File f) {
        try { if (f != null && f.exists()) f.delete(); } catch (Exception ignored) {}
    }

    private void log(String batchDate, String step, String status, String fileName, String message) {
        mapper.insertBenepiaBatchLog(batchDate, step, status, fileName, message);
    }

    private String shortMsg(Exception e) {
        String m = e.getMessage();
        if (m == null) return e.getClass().getSimpleName();
        return m.length() > 500 ? m.substring(0, 500) : m;
    }

    private static String nz(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private static int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(v));
    }

    @FunctionalInterface
    private interface FileNameProvider {
        String fileName(int seq);
    }
}