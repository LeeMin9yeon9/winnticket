package kr.co.winnticket.sms.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateRenderService {

    public String render(String template, Map<String,String> vars) {
        String result = template;
        for (Map.Entry<String,String> e : vars.entrySet()) {
            result = result.replace(
                    "{" + e.getKey() + "}",
                    e.getValue() == null ? "" : e.getValue()
            );
        }
        return result;
    }
}

