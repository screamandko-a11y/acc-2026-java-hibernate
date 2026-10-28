package bootcamp.hibernate_practical.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;

@Configuration
public class ExternalApiConfig {

    @Bean
    public RestClient externalApiRestClient(
            @Value("${external.api.base-url}") String baseUrl,
            @Value("${external.api.token}") String token
    ) {
        if (token.isBlank()) {
            throw new IllegalStateException(
                    "EXTERNAL_API_TOKEN must not be blank"
            );
        }

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(
                            HttpURLConnection connection,
                            String httpMethod
                    ) throws IOException {
                        super.prepareConnection(connection, httpMethod);
                        connection.setInstanceFollowRedirects(false);
                    }
                };

        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> headers.setBearerAuth(token))
                .build();
    }
}
