package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.ExternalStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestClient externalApiRestClient;

    public ExternalStatusResponse getStatus() {
        return externalApiRestClient.get()
                .uri("/partner/status")
                .retrieve()
                .body(ExternalStatusResponse.class);
    }
}
