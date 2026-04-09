package medicalclinicproxy.proxy.service;

import medicalclinicproxy.client.AppointmentClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {

    AppointmentService appointmentService;
    AppointmentClient appointmentClient;

    @BeforeEach
    void setup() {
        this.appointmentClient = Mockito.mock(AppointmentClient.class);
        this.appointmentService = new AppointmentService(appointmentClient);
    }

    @Test
    void getAll_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<AppointmentDto> expected = pageOf(new AppointmentDto(1L, null, null, null, null));
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 16, 0);

        when(appointmentClient.getAll("cardiology", start, end, 0, 10)).thenReturn(expected);

        PageableContentDto<AppointmentDto> result = appointmentService.getAll("cardiology", start, end, 0, 10);

        assertSame(expected, result);
        verify(appointmentClient).getAll("cardiology", start, end, 0, 10);
    }

    @Test
    void getAll_shouldPropagateExceptionFromClient() {
        when(appointmentClient.getAll(any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(MedicalclinicException.class,
                () -> appointmentService.getAll(null, null, null, 0, 10));
    }

    @Test
    void getAllAppointmentsByPatientId_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<AppointmentDto> expected = pageOf(new AppointmentDto(2L, null, null, null, null));
        when(appointmentClient.getAllByPatientId(1L, 0, 10)).thenReturn(expected);

        PageableContentDto<AppointmentDto> result = appointmentService.getAllAppointmentsByPatientId(1L, 0, 10);

        assertSame(expected, result);
        verify(appointmentClient).getAllByPatientId(1L, 0, 10);
    }

    @Test
    void getAllAppointmentsByPatientId_shouldPropagateExceptionFromClient() {
        when(appointmentClient.getAllByPatientId(eq(99L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Patient not found", HttpStatus.NOT_FOUND));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentService.getAllAppointmentsByPatientId(99L, 0, 10));

        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void getAllAppointmentsByDoctorId_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<AppointmentDto> expected = pageOf(new AppointmentDto(3L, null, null, null, null));
        when(appointmentClient.getAllByDoctorId(5L, 0, 10)).thenReturn(expected);

        PageableContentDto<AppointmentDto> result = appointmentService.getAllAppointmentsByDoctorId(5L, 0, 10);

        assertSame(expected, result);
        verify(appointmentClient).getAllByDoctorId(5L, 0, 10);
    }

    @Test
    void getAllAppointmentsByDoctorId_shouldPropagateExceptionFromClient() {
        when(appointmentClient.getAllByDoctorId(eq(99L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Doctor not found", HttpStatus.NOT_FOUND));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentService.getAllAppointmentsByDoctorId(99L, 0, 10));

        assertEquals("Doctor not found", ex.getMessage());
    }

    @Test
    void assignPatientToAppointment_shouldDelegateToClientAndReturnResult() {
        AppointmentDto expected = new AppointmentDto(1L, null, null, null, null);
        when(appointmentClient.assignPatientToAppointment(1L, 2L)).thenReturn(expected);

        AppointmentDto result = appointmentService.assignPatientToAppointment(1L, 2L);

        assertSame(expected, result);
        verify(appointmentClient).assignPatientToAppointment(1L, 2L);
    }

    @Test
    void assignPatientToAppointment_shouldPropagateExceptionFromClient() {
        when(appointmentClient.assignPatientToAppointment(eq(1L), eq(1L)))
                .thenThrow(new MedicalclinicException("Appointment already taken", HttpStatus.CONFLICT));

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentService.assignPatientToAppointment(1L, 1L));

        assertEquals("Appointment already taken", ex.getMessage());
    }

    @Test
    void getAvailableAppointmentsByDoctorId_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<AppointmentDto> expected = pageOf(new AppointmentDto(10L, null, null, null, null));
        when(appointmentClient.getAvailableAppointmentsByDoctorId(3L, 0, 10)).thenReturn(expected);

        PageableContentDto<AppointmentDto> result = appointmentService.getAvailableAppointmentsByDoctorId(3L, 0, 10);

        assertSame(expected, result);
        verify(appointmentClient).getAvailableAppointmentsByDoctorId(3L, 0, 10);
    }

    @Test
    void getAvailableAppointmentsByDoctorId_shouldPropagateExceptionFromClient() {
        when(appointmentClient.getAvailableAppointmentsByDoctorId(eq(99L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Doctor not found", HttpStatus.NOT_FOUND));

        assertThrows(MedicalclinicException.class,
                () -> appointmentService.getAvailableAppointmentsByDoctorId(99L, 0, 10));
    }

    @Test
    void getAvailableAppointments_shouldDelegateToClientAndReturnResult() {
        PageableContentDto<AppointmentDto> expected = pageOf(new AppointmentDto(5L, null, null, null, null));
        LocalDate date = LocalDate.of(2024, 6, 15);

        when(appointmentClient.getAvailableAppointments("neurology", date, null, null, 0, 10))
                .thenReturn(expected);

        PageableContentDto<AppointmentDto> result =
                appointmentService.getAvailableAppointments("neurology", date, null, null, 0, 10);

        assertSame(expected, result);
        verify(appointmentClient).getAvailableAppointments("neurology", date, null, null, 0, 10);
    }

    @Test
    void getAvailableAppointments_shouldPropagateExceptionFromClient() {
        when(appointmentClient.getAvailableAppointments(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Search service unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(MedicalclinicException.class,
                () -> appointmentService.getAvailableAppointments(null, null, null, null, 0, 10));
    }

    @Test
    void deleteAppointment_shouldDelegateToClient() {
        doNothing().when(appointmentClient).deleteAppointment(1L);

        assertDoesNotThrow(() -> appointmentService.deleteAppointment(1L));

        verify(appointmentClient).deleteAppointment(1L);
    }

    @Test
    void deleteAppointment_shouldPropagateExceptionFromClient() {
        doThrow(new MedicalclinicException("Appointment not found", HttpStatus.NOT_FOUND))
                .when(appointmentClient).deleteAppointment(99L);

        MedicalclinicException ex = assertThrows(MedicalclinicException.class,
                () -> appointmentService.deleteAppointment(99L));

        assertEquals("Appointment not found", ex.getMessage());
    }

    private PageableContentDto<AppointmentDto> pageOf(AppointmentDto... items) {
        return new PageableContentDto<>(items.length, 1, 0, List.of(items));
    }
}