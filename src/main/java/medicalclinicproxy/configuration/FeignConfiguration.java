package medicalclinicproxy.configuration;

import feign.Client;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfiguration {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }

    @Bean
    public Client feignClient(){
        return new feign.okhttp.OkHttpClient();
    }

//    @Bean
//    public ErrorDecoder errorDecoder() {
//        return new GithubErrorDecoder();
//    }
}