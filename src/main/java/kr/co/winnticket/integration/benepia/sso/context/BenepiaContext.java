package kr.co.winnticket.integration.benepia.sso.context;

import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;

public class BenepiaContext {

    private static final ThreadLocal<BenepiaDecryptedParamDto> context = new ThreadLocal<>();

    public static void set(BenepiaDecryptedParamDto dto){
        context.set(dto);
    }

    public static BenepiaDecryptedParamDto get(){
        return context.get();
    }

    public static void clear(){
        context.remove();
    }

}
