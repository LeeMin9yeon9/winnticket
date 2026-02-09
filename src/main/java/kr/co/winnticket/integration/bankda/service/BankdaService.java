package kr.co.winnticket.integration.bankda.service;

import kr.co.winnticket.integration.bankda.client.BankdaClient;
import kr.co.winnticket.integration.bankda.dto.BankdaRequest;
import kr.co.winnticket.integration.bankda.dto.BankdaTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankdaService {

    private final BankdaClient client;

    public List<BankdaTransaction> fetchToday() {

        String today = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        BankdaRequest req = BankdaRequest.builder()
                .datefrom(today)
                .dateto(today)
                .istest("y") // 테스트 추천
                .build();

        return client.getTransactions(req);
    }
}
