package medicalclinicproxy.client;

import medicalclinicproxy.configuration.AppointmentClientFallbackFactory;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

@FeignClient(value = "medicalclinic", contextId = "appointmentClient",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = AppointmentClientFallbackFactory.class, path = "/appointments")

public interface AppointmentClient {

    String DEFAULT_PAGE = "0";
    String DEFAULT_SIZE = "10";

    @GetMapping
    PageableContentDto<AppointmentDto> getAll(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    );

    @GetMapping("/patient/{patientId}")
    PageableContentDto<AppointmentDto> getAllByPatientId(
            @PathVariable("patientId") Long patientId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    );

    @GetMapping("/doctor/{doctorId}")
    PageableContentDto<AppointmentDto> getAllByDoctorId(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    );

    @PatchMapping("/{appointmentId}/patient/{patientId}")
    AppointmentDto assignPatientToAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("patientId") Long patientId
    );

    @GetMapping("/doctor/{doctorId}/available")
    PageableContentDto<AppointmentDto> getAvailableAppointmentsByDoctorId(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    );

    @GetMapping("/available")
    PageableContentDto<AppointmentDto> getAvailableAppointments(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    );

    @DeleteMapping("/{appointmentId}")
    void deleteAppointment(
            @PathVariable("appointmentId") Long appointmentId
    );
}