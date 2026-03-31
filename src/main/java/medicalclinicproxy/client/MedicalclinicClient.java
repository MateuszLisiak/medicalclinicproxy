package medicalclinicproxy.client;

import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "medicalclinic", url = "${spring.cloud.openfeign.client.config.medical-clinic.url}")

public interface MedicalclinicClient {
    @GetMapping("/appointments/patient/{patientId}")
    PageableContentDto<AppointmentDto> getAllByPatientId(
            @PathVariable("patientId") Long patientId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @PatchMapping("/appointments/{appointmentId}/{patientId}")
    AppointmentDto assignPatientToAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("patientId") Long patientId
    );
}