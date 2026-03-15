package kr.co.winnticket.integration.benepia.sso.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class BenepiaContextFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        try{

            chain.doFilter(request, response);

        } finally {

            BenepiaContext.clear();

        }
    }
}
