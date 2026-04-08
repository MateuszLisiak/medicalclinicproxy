package medicalclinicproxy.proxy.controller;

import medicalclinicproxy.controller.DoctorController;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    DoctorService doctorService;


    @Test
    void getDoctors_AllParametersProvided_ReturnsOk() throws Exception {
        // given
        DoctorDto doctor = new DoctorDto(1L, "john@gmail.com", "John", "Doe", "CARDIOLOGY");
        PageableContentDto<DoctorDto> pageResponse = pageOf(doctor);
        when(doctorService.getDoctors(eq("CARDIOLOGY"), eq(0), eq(10)))
                .thenReturn(pageResponse);
        // when & then
        mockMvc.perform(get("/doctors")
                        .param("specialization", "CARDIOLOGY")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content[0].specialization").value("CARDIOLOGY"));
        verify(doctorService).getDoctors("CARDIOLOGY", 0, 10);
    }

    @Test
    void getDoctors_shouldReturn404_whenServiceThrowsNotFound() throws Exception {
        // given
        String unknownSpec = "unknown";
        when(doctorService.getDoctors(eq(unknownSpec), anyInt(), anyInt()))
                .thenThrow(new MedicalclinicException("Specialization not found", HttpStatus.NOT_FOUND));
        // when & then
        mockMvc.perform(get("/doctors")
                        .param("specialization", unknownSpec))
                .andExpect(status().isNotFound());
        verify(doctorService).getDoctors(eq(unknownSpec), anyInt(), anyInt());
    }

    private PageableContentDto<DoctorDto> pageOf(DoctorDto... items) {
        return new PageableContentDto<>(items.length, 1, 0, List.of(items));
    }
}