package kr.co.winnticket.siteinfo.companyinfo.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyIntroResponse {
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private LocalDate establishedDate;
    private String address;
    private String tel;
    private String email;
    private String companyIntroduction;
}
