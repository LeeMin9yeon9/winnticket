package kr.co.winnticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan(basePackages = "kr.co.winnticket")
public class WinticketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WinticketApplication.class, args);
    }

}
