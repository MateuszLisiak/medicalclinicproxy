package medicalclinicproxy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.PatientService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/appointments")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/patient/{patientId}")
    public PageableContentDto<AppointmentDto> getAllAppointmentsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Received GET request for appointments patientId:'{}', page: {}, size: {}", patientId, page, size);
        PageableContentDto<AppointmentDto> result = patientService.getAllAppointmentsByPatientId(patientId, page, size);
        log.info("Returning {} appointments for patientId: '{}'", result.content().size(), patientId);
        return result;
    }

    @PatchMapping("/{appointmentId}/{patientId}")
    public AppointmentDto assignPatientToAppointment(@PathVariable Long appointmentId, @PathVariable Long patientId){
        log.info("Received PATCH request to assign patientId: '{}' to appointmentId: '{}'", patientId, appointmentId);
        AppointmentDto updatedAppointment = patientService.assignPatientToAppointment(appointmentId, patientId);
        log.info("Successfully assigned patientId: '{}' to appointmentId: '{}'", patientId, appointmentId);
        return updatedAppointment;
    }
}