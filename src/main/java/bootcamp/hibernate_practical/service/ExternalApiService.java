package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.ExternalStatusResponse;
import bootcamp.hibernate_practical.exceptions.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestClient externalApiRestClient;

    public ExternalStatusResponse getStatus() {
        try {
            ExternalStatusResponse response = externalApiRestClient.get()
                    .uri("/partner/status")
                    .retrieve()
                    .body(ExternalStatusResponse.class);

            if (response == null || !"ok".equals(response.status())) {
                throw new ExternalApiException(
                        HttpStatus.BAD_GATEWAY,
                        "Invalid response from external API"
                );
            }

            return response;

        } catch (RestClientResponseException exception) {
            String message = switch (exception.getStatusCode().value()) {
                case 401 -> "External API authentication failed";
                case 403 -> "External API denied access";
                default -> "External API returned an error";
            };

            throw new ExternalApiException(
                    HttpStatus.BAD_GATEWAY,
                    message
            );

        } catch (RestClientException exception) {
            if (isTimeout(exception)) {
                throw new ExternalApiException(
                        HttpStatus.GATEWAY_TIMEOUT,
                        "External API timed out"
                );
            }

            if (exception instanceof ResourceAccessException) {
                throw new ExternalApiException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "External API is unavailable"
                );
            }

            throw new ExternalApiException(
                    HttpStatus.BAD_GATEWAY,
                    "Invalid response from external API"
            );
        }
    }

    private boolean isTimeout(Throwable exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof HttpTimeoutException
                    || cause instanceof SocketTimeoutException) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
