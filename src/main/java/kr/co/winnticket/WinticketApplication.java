package kr.co.winnticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
//@ComponentScan(basePackages = "kr.co.winnticket")
public class WinticketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WinticketApplication.class, args);
    }

}
