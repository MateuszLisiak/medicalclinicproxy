package medicalclinicproxy.proxy.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import medicalclinicproxy.client.DoctorClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
class DoctorClientTest {

    @Autowired
    DoctorClient doctorClient;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void reset() {
        wireMockServer.resetAll();
    }

    @Test
    void getDoctorBySpecialization_shouldDeserializeResponse_on200() throws Exception {
        DoctorDto doctor = new DoctorDto(1L, "jan@test.com", "Jan", "Kowalski", "cardiology");
        PageableContentDto<DoctorDto> body = new PageableContentDto<>(1L, 1, 0, List.of(doctor));

        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(okJson(objectMapper.writeValueAsString(body))));

        PageableContentDto<DoctorDto> result = doctorClient.getDoctors("cardiology", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("cardiology", result.content().get(0).specialization());
        assertEquals("jan@test.com", result.content().get(0).email());
    }

    @Test
    void getDoctorBySpecialization_shouldSendAllQueryParams() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("neurology"))
                .withQueryParam("page", equalTo("2"))
                .withQueryParam("size", equalTo("5"))
                .willReturn(okJson("{\"totalElements\":0,\"totalPages\":0,\"currentPage\":0,\"content\":[]}")));

        doctorClient.getDoctors("neurology", 2, 5);

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("neurology"))
                .withQueryParam("page", equalTo("2"))
                .withQueryParam("size", equalTo("5")));
    }

    @Test
    void getDoctorBySpecialization_shouldUseGetMethod() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(okJson("{\"totalElements\":0,\"totalPages\":0,\"currentPage\":0,\"content\":[]}")));

        doctorClient.getDoctors("cardiology", 0, 10);

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/doctors")));
    }

    @Test
    void getDoctorBySpecialization_errorDecoder_shouldParseMessageFromBody_on404() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("unknown"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Specialization not found\"}")));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> doctorClient.getDoctors("unknown", 0, 10));

        assertTrue(ex.getMessage().contains("unavailable"),
                "Unexpected message: " + ex.getMessage());
    }

    @Test
    void getDoctorBySpecialization_errorDecoder_shouldUseDefaultMessage_whenBodyIsEmpty() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> doctorClient.getDoctors("cardiology", 0, 10));

        assertNotNull(ex.getMessage());
    }

    @Test
    void getDoctorBySpecialization_errorDecoder_shouldUseDefaultMessage_whenBodyIsNotJson() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("bad request")));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> doctorClient.getDoctors("cardiology", 0, 10));

        assertNotNull(ex.getMessage());
    }

    @Test
    void getDoctorBySpecialization_shouldRetry_on503() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(aResponse().withStatus(503)));
        // when
        assertThrows(Exception.class,
                () -> doctorClient.getDoctors("cardiology", 0, 10));
        // then
        int count = wireMockServer.findAll(getRequestedFor(urlPathEqualTo("/doctors"))).size();
        assertEquals(3, count);
    }

    @Test
    void getDoctorBySpecialization_fallback_shouldThrowServiceUnavailable_on503() {
        wireMockServer.stubFor(get(urlPathEqualTo("/doctors"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .willReturn(aResponse().withStatus(503)));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> doctorClient.getDoctors("cardiology", 0, 10));

        assertTrue(ex.getMessage().contains("unavailable"),
                "Unexpected message: " + ex.getMessage());
    }
}