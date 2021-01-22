package jpastudy.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class DatajpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatajpaApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        //실제로는 세션 데이터나 시큐리티 컨텍스트 데이터값으로 처리.
        //자세한 내용은 공식 문서 참조
        //[Auditing](https://docs.spring.io/spring-data/jpa/docs/2.4.3/reference/html/#auditing)
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
