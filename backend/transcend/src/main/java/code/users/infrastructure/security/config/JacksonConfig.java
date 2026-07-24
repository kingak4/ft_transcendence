package code.users.infrastructure.security.config;

import code.users.domain.model.FriendId;
import code.users.infrastructure.jackson.FriendIdKeySerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public SimpleModule friendIdModule() {
        SimpleModule module = new SimpleModule();
        module.addKeySerializer(FriendId.class, new FriendIdKeySerializer());
        return module;
    }
}
