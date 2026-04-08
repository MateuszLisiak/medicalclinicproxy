package medicalclinicproxy.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.exception.MedicalclinicException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MedicalclinicErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = getBody(response);

        if (response.status() == 503) {
            return new RetryableException(
                    response.status(),
                    "Medical clinic service unavailable",
                    response.request().httpMethod(),
                    50L,
                    response.request()
            );
        }

        String message = "Unknown error from medical clinic";
        HttpStatus status = HttpStatus.resolve(response.status());

        if (!body.isEmpty()) {
            try {
                JsonNode node = objectMapper.readTree(body);
                if (node.has("message")) {
                    message = node.get("message").asText();
                }
            } catch (IOException e) {
                log.warn("Could not parse error response body: {}", e.getMessage());
            }
        }

        return new MedicalclinicException(message, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getBody(Response response) {
        if (response.body() == null) return "";
        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Unable to read response body";
        }
    }
}