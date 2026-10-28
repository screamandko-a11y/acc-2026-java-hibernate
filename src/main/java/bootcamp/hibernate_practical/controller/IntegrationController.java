package bootcamp.hibernate_practical.controller;

import bootcamp.hibernate_practical.dto.ExternalStatusResponse;
import bootcamp.hibernate_practical.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final ExternalApiService externalApiService;

    @GetMapping("/status")
    public ExternalStatusResponse getStatus() {
        return externalApiService.getStatus();
    }
}
