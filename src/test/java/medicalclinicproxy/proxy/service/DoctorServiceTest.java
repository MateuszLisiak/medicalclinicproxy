package medicalclinicproxy.proxy.service;

import medicalclinicproxy.client.DoctorClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    DoctorService doctorService;
    DoctorClient doctorClient;

    @BeforeEach
    void setup() {
        this.doctorClient = Mockito.mock(DoctorClient.class);
        this.doctorService = new DoctorService(doctorClient);
    }

    @Test
    void getDoctorsBySpecialization_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<DoctorDto> expected = pageOf(
                new DoctorDto(1L, "jan@test.com", "Jan", "Kowalski", "cardiology"));

        when(doctorClient.getDoctors("cardiology", 0, 10)).thenReturn(expected);

        PageableContentDto<DoctorDto> result = doctorService.getDoctors("cardiology", 0, 10);

        assertSame(expected, result);
        verify(doctorClient).getDoctors("cardiology", 0, 10);
    }

    @Test
    void getDoctorsBySpecialization_shouldPassAllParamsToClient() {
        PageableContentDto<DoctorDto> expected = pageOf();
        when(doctorClient.getDoctors("neurology", 2, 5)).thenReturn(expected);

        doctorService.getDoctors("neurology", 2, 5);

        verify(doctorClient).getDoctors("neurology", 2, 5);
    }

    @Test
    void getDoctorsBySpecialization_shouldPropagateExceptionFromClient() {
        when(doctorClient.getDoctors(eq("unknown"), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException(
                        "Doctor service is currently unavailable.", HttpStatus.SERVICE_UNAVAILABLE));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> doctorService.getDoctors("unknown", 0, 10));

        assertTrue(ex.getMessage().contains("unavailable"));
    }

    private PageableContentDto<DoctorDto> pageOf(DoctorDto... items) {
        return new PageableContentDto<>(items.length, 1, 0, List.of(items));
    }
}