package medicalclinicproxy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";
    private final AppointmentService appointmentService;

    @GetMapping
    public PageableContentDto<AppointmentDto> getAllAppointments(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Proxy GET /appointments | specialization={}, startDate={}, endDate={}, page={}, size={}",
                specialization, startDate, endDate, page, size);

        PageableContentDto<AppointmentDto> result =
                appointmentService.getAll(specialization, startDate, endDate, page, size);

        log.info("Returning {} appointments from proxy", result.content() != null ? result.content().size() : 0);
        return result;
    }

    @GetMapping("/patient/{patientId}")
    public PageableContentDto<AppointmentDto> getAppointmentsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Received GET request for appointments patientId:'{}', page: {}, size: {}", patientId, page, size);
        PageableContentDto<AppointmentDto> result = appointmentService.getAllAppointmentsByPatientId(patientId, page, size);
        log.info("Returning {} appointments for patientId: '{}'", result.content().size(), patientId);
        return result;
    }

    @GetMapping("/doctor/{doctorId}")
    public PageableContentDto<AppointmentDto> getAppointmentsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Received GET request for appointments doctorId:'{}', page: {}, size: {}", doctorId, page, size);
        PageableContentDto<AppointmentDto> result = appointmentService.getAllAppointmentsByDoctorId(doctorId, page, size);
        log.info("Returning {} appointments for doctorId: '{}'", result.content().size(), doctorId);
        return result;
    }

    @GetMapping("/available/doctor/{doctorId}")
    public PageableContentDto<AppointmentDto> getAvailableAppointmentsByDoctorId(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Received GET request for available appointments for doctorId: '{}', page: {}, size: {}",
                doctorId, page, size);
        PageableContentDto<AppointmentDto> result = appointmentService.getAvailableAppointmentsByDoctorId(doctorId, page, size);
        log.info("Returning {} available appointments for doctorId: '{}'", result.content().size(), doctorId);
        return result;
    }

    @GetMapping("/available")
    public PageableContentDto<AppointmentDto> getAvailableAppointments(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        PageableContentDto<AppointmentDto> result = appointmentService.getAvailableAppointments(
                specialization, date, startDate, endDate, page, size);

        int sizeSafe = result.content() != null ? result.content().size() : 0;

        if (date != null) {
            log.info("Returning {} available appointments for specialization '{}' on single date {}",
                    sizeSafe, specialization, date);
        } else if (startDate != null && endDate != null) {
            log.info("Returning {} available appointments for specialization '{}' from {} to {}",
                    sizeSafe, specialization, startDate, endDate);
        } else {
            log.info("Returning {} available appointments for specialization '{}', no date filter applied",
                    sizeSafe, specialization);
        }
        return result;
    }

    @PatchMapping("/{appointmentId}/patient/{patientId}")
    public AppointmentDto assignPatientToAppointment(
            @PathVariable Long appointmentId,
            @PathVariable Long patientId
    ) {
        log.info("Received PATCH request to assign patientId: '{}' to appointmentId: '{}'", patientId, appointmentId);
        AppointmentDto updatedAppointment = appointmentService.assignPatientToAppointment(appointmentId, patientId);
        log.info("Successfully assigned patientId: '{}' to appointmentId: '{}'", patientId, appointmentId);
        return updatedAppointment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{appointmentId}")
    void deleteAppointment(
            @PathVariable Long appointmentId
    ) {
        log.info("Received DELETE request for appointment for appointmentId: '{}'", appointmentId);
        appointmentService.deleteAppointment(appointmentId);
    }
}