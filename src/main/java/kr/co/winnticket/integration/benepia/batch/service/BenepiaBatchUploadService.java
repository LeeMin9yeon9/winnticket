package kr.co.winnticket.integration.benepia.batch.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.batch.dto.BenepiaIdsJson;
import kr.co.winnticket.integration.benepia.batch.dto.BenepiaProductsJson;
import kr.co.winnticket.integration.benepia.batch.mapper.BenepiaBatchMapper;
import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenepiaBatchUploadService {

    private final BenepiaBatchMapper mapper;
    private final BenepiaProperties props;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    public void executeBatch() throws Exception {

        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = "0";

        String idsFile = props.getKcpCoCd()+"_ids_"+today+"_"+seq+".json";
        String productsFile = props.getKcpCoCd()+"_products_"+today+"_"+seq+".json";

        // 1) IDS 업로드
        uploadIds(idsFile);

        // 2) PRODUCTS 업로드
        uploadProducts(productsFile);

        // 3) PRODUCTS DONE 호출
        doneProducts(productsFile);
    }

    private void uploadIds(String fileName) throws Exception {

        List<Map<String,Object>> rows = mapper.selectBenepiaIds();
        List<BenepiaIdsJson.IdItem> list = new ArrayList<>();

        for(Map<String,Object> r : rows){
            BenepiaIdsJson.IdItem item = new BenepiaIdsJson.IdItem();
            item.coopCoCd = r.get("coopCoCd").toString();
            item.prdId    = r.get("prdId").toString();
            list.add(item);
        }

        BenepiaIdsJson root = new BenepiaIdsJson();
        root.setProductIds(list);

        sendMultipart("/ids", fileName, om.writeValueAsBytes(root));
    }

    private void uploadProducts(String fileName) throws Exception {

        List<Map<String,Object>> rows = mapper.selectBenepiaProducts();
        List<BenepiaProductsJson.ProductWrap> list = new ArrayList<>();

        for(Map<String,Object> r : rows){

            BenepiaProductsJson.Product p = new BenepiaProductsJson.Product();
            p.prdId = r.get("prdId").toString();
            p.prdNm = r.get("prdNm").toString();
            p.orgnPrc = Integer.parseInt(r.get("orgnPrc").toString());
            p.salePrc = Integer.parseInt(r.get("salePrc").toString());
            p.prdImgUrl = r.get("prdImgUrl").toString();
            p.prdDtlUrlTyp = r.get("prdDtlUrlTyp").toString();
            p.prdDtlUrl = r.get("prdDtlUrl").toString();
            p.prdMobDtlUrlTyp = r.get("prdMobDtlUrlTyp").toString();
            p.prdMobDtlUrl = r.get("prdMobDtlUrl").toString();
            p.keyword="";
            p.prdType="10";
            p.prdSubTitle="";
            p.prdDesc = r.get("prdDesc")==null?"":r.get("prdDesc").toString();
            p.regDate = r.get("regDate").toString();
            p.updDate = r.get("updDate").toString();
            p.param1=""; p.param2=""; p.param3="";

            BenepiaProductsJson.Travel t = new BenepiaProductsJson.Travel();
            t.prdGb="03";
            t.nationalCd="KR";
            t.regionCd = r.get("regionCd")==null?"":r.get("regionCd").toString();

            BenepiaProductsJson.Ticket tk = new BenepiaProductsJson.Ticket();
            tk.ticketType = r.get("ticketType")==null?"":r.get("ticketType").toString();
            tk.expireInfo = r.get("expireInfo")==null?"":r.get("expireInfo").toString();
            tk.ticketPlace = r.get("ticketPlace")==null?"":r.get("ticketPlace").toString();

            BenepiaProductsJson.ProductWrap w = new BenepiaProductsJson.ProductWrap();
            w.setProduct(p);
            w.setTravel(t);
            w.setTicket(tk);

            list.add(w);
        }

        BenepiaProductsJson root = new BenepiaProductsJson();
        root.setProducts(list);

        sendMultipart("/products", fileName, om.writeValueAsBytes(root));
    }

    private void sendMultipart(String path, String fileName, byte[] data){

        String url = props.getBatchBaseUrl()
                + "/v1/partners/"
                + props.getKcpCoCd()
                + path;

        log.info(">>> BENEP API CALL URL = {}", url);

        ByteArrayResource file = new ByteArrayResource(data){
            @Override public String getFilename(){ return fileName; }
        };

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        rest.postForEntity(url,new HttpEntity<>(body,headers),String.class);
    }

    private void doneProducts(String fileName){

        String url = props.getBatchBaseUrl()
                + "/v1/partners/"
                + props.getKcpCoCd()
                + "/products/done/"
                + props.getCustCoCd();

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("fileName", fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        rest.postForEntity(url,new HttpEntity<>(body,headers),String.class);
    }}
