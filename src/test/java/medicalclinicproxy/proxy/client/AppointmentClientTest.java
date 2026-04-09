package medicalclinicproxy.proxy.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import medicalclinicproxy.client.AppointmentClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.model.dto.AppointmentDto;
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
class AppointmentClientTest {

    @Autowired
    AppointmentClient appointmentClient;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void reset() {
        wireMockServer.resetAll();
    }

    @Test
    void getAll_shouldDeserializeResponse_on200() throws Exception {
        PageableContentDto<AppointmentDto> body = new PageableContentDto<>(
                1L, 1, 0, List.of(new AppointmentDto(1L, null, null, null, null)));

        wireMockServer.stubFor(get(urlPathEqualTo("/appointments"))
                .willReturn(okJson(objectMapper.writeValueAsString(body))));

        PageableContentDto<AppointmentDto> result = appointmentClient.getAll(null, null, null, 0, 10);

        assertEquals(1, result.content().size());
        assertEquals(1L, result.content().get(0).id());
    }

    @Test
    void getAll_shouldSendQueryParams() {
        wireMockServer.stubFor(get(urlPathEqualTo("/appointments"))
                .withQueryParam("specialization", equalTo("cardiology"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("5"))
                .willReturn(okJson("{\"totalElements\":0,\"totalPages\":0,\"currentPage\":0,\"content\":[]}")));

        appointmentClient.getAll("cardiology", null, null, 0, 5);

        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/appointments"))
                .withQueryParam("specialization", equalTo("cardiology")));
    }

    @Test
    void getAll_fallback_shouldThrowServiceUnavailable_on503() {
        wireMockServer.stubFor(get(urlPathEqualTo("/appointments"))
                .willReturn(aResponse().withStatus(503)));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentClient.getAll(null, null, null, 0, 10));

        assertTrue(ex.getMessage().toLowerCase().contains("unavailable"));
    }

    @Test
    void getAllByPatientId_shouldDeserializeResponse_on200() throws Exception {
        PageableContentDto<AppointmentDto> body = new PageableContentDto<>(
                1L, 1, 0, List.of(new AppointmentDto(2L, null, null, null, null)));

        wireMockServer.stubFor(get(urlPathEqualTo("/appointments/patient/1"))
                .willReturn(okJson(objectMapper.writeValueAsString(body))));

        PageableContentDto<AppointmentDto> result = appointmentClient.getAllByPatientId(1L, 0, 10);

        assertEquals(2L, result.content().get(0).id());
    }

    @Test
    void getAllByPatientId_errorDecoder_shouldParseMessageFromBody_on404() {
        wireMockServer.stubFor(get(urlPathEqualTo("/appointments/patient/99"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Patient not found\"}")));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentClient.getAllByPatientId(99L, 0, 10));

        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void assignPatientToAppointment_shouldDeserializeResponse_on200() throws Exception {
        AppointmentDto body = new AppointmentDto(1L, null, null, null, null);

        wireMockServer.stubFor(patch(urlPathEqualTo("/appointments/1/patient/2"))
                .willReturn(okJson(objectMapper.writeValueAsString(body))));

        AppointmentDto result = appointmentClient.assignPatientToAppointment(1L, 2L);

        assertEquals(1L, result.id());
    }

    @Test
    void assignPatientToAppointment_errorDecoder_shouldParseMessageFromBody_on409() {
        wireMockServer.stubFor(patch(urlPathEqualTo("/appointments/1/patient/1"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Appointment already taken\"}")));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentClient.assignPatientToAppointment(1L, 1L));

        assertEquals("Appointment already taken", ex.getMessage());
    }

    @Test
    void assignPatientToAppointment_fallback_shouldThrowAssignmentFailed_on503() {
        wireMockServer.stubFor(patch(urlPathEqualTo("/appointments/1/patient/1"))
                .willReturn(aResponse().withStatus(503)));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentClient.assignPatientToAppointment(1L, 1L));

        assertTrue(ex.getMessage().contains("Assignment failed"));
    }

    @Test
    void deleteAppointment_shouldComplete_on204() {
        wireMockServer.stubFor(delete(urlPathEqualTo("/appointments/1"))
                .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> appointmentClient.deleteAppointment(1L));

        wireMockServer.verify(1, deleteRequestedFor(urlPathEqualTo("/appointments/1")));
    }

    @Test
    void deleteAppointment_fallback_shouldThrowDeletionFailed_on503() {
        wireMockServer.stubFor(delete(urlPathEqualTo("/appointments/1"))
                .willReturn(aResponse().withStatus(503)));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentClient.deleteAppointment(1L));

        assertTrue(ex.getMessage().contains("Deletion failed") || ex.getMessage().contains("unavailable"));
    }
}