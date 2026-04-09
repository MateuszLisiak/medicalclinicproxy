package medicalclinicproxy.proxy.controller;

import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void getAll_shouldReturn200And503OnError() throws Exception {
        when(appointmentService.getAll(null, null, null, 0, 10))
                .thenReturn(pageOf(new AppointmentDto(1L, null, null, null, null)));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        when(appointmentService.getAll(any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void getAppointmentsByPatientId_shouldReturn200And404() throws Exception {
        when(appointmentService.getAllAppointmentsByPatientId(1L, 0, 10))
                .thenReturn(pageOf(new AppointmentDto(2L, null, null, null, null)));

        mockMvc.perform(get("/appointments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2L));

        when(appointmentService.getAllAppointmentsByPatientId(eq(99L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Patient not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/appointments/patient/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAppointmentsByDoctorId_shouldReturn200And404() throws Exception {
        when(appointmentService.getAllAppointmentsByDoctorId(5L, 0, 10))
                .thenReturn(pageOf(new AppointmentDto(3L, null, null, null, null)));

        mockMvc.perform(get("/appointments/doctor/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3L));

        when(appointmentService.getAllAppointmentsByDoctorId(eq(99L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Doctor not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/appointments/doctor/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableByDoctorId_shouldReturn200And503() throws Exception {
        when(appointmentService.getAvailableAppointmentsByDoctorId(3L, 0, 10))
                .thenReturn(pageOf(new AppointmentDto(10L, null, null, null, null)));

        mockMvc.perform(get("/appointments/available/doctor/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10L));

        when(appointmentService.getAvailableAppointmentsByDoctorId(eq(1L), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        mockMvc.perform(get("/appointments/available/doctor/1"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void getAvailableAppointments_shouldReturn200And503() throws Exception {
        when(appointmentService.getAvailableAppointments(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(pageOf(new AppointmentDto(5L, null, null, null, null)));

        mockMvc.perform(get("/appointments/available"))
                .andExpect(status().isOk());

        when(appointmentService.getAvailableAppointments(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Search service unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        mockMvc.perform(get("/appointments/available"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void assignPatient_shouldReturn200AndHandleErrors() throws Exception {
        when(appointmentService.assignPatientToAppointment(1L, 2L))
                .thenReturn(new AppointmentDto(1L, null, null, null, null));

        mockMvc.perform(patch("/appointments/1/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        when(appointmentService.assignPatientToAppointment(eq(99L), anyLong()))
                .thenThrow(new MedicalclinicException("Appointment not found", HttpStatus.NOT_FOUND));
        mockMvc.perform(patch("/appointments/99/patient/1"))
                .andExpect(status().isNotFound());

        when(appointmentService.assignPatientToAppointment(eq(1L), eq(1L)))
                .thenThrow(new MedicalclinicException("Appointment already taken", HttpStatus.CONFLICT));
        mockMvc.perform(patch("/appointments/1/patient/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteAppointment_shouldReturn204And404() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/appointments/1"))
                .andExpect(status().isNoContent());

        doThrow(new MedicalclinicException("Appointment not found", HttpStatus.NOT_FOUND))
                .when(appointmentService).deleteAppointment(99L);

        mockMvc.perform(delete("/appointments/99"))
                .andExpect(status().isNotFound());
    }

    @SafeVarargs
    private <T> PageableContentDto<T> pageOf(T... items) {
        return new PageableContentDto<>(items.length, 1, 0, List.of(items));
    }
}