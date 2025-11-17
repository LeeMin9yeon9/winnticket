package kr.co.winnticket.common.config;

import kr.co.winnticket.common.typehandler.EnumTypeHandler;
import kr.co.winnticket.common.typehandler.UUIDTypeHandler;
import kr.co.winnticket.common.enums.PostType;
import kr.co.winnticket.common.enums.QnaStatus;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MybatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry() // UUID
                    .register(UUID.class, new UUIDTypeHandler());
            // 커뮤니티
            configuration.getTypeHandlerRegistry() // PostType
                    .register(PostType.class, new EnumTypeHandler<>(PostType.class));
            configuration.getTypeHandlerRegistry() // QnaStatus
                    .register(QnaStatus.class, new EnumTypeHandler<>(QnaStatus.class));
        };
    }
}
