package medicalclinicproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.client.MedicalclinicClient;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {
    private final MedicalclinicClient medicalclinicClient;

    public PageableContentDto<AppointmentDto> getAllAppointmentsByPatientId(Long patientId, int page, int size) {
        return medicalclinicClient.getAllByPatientId(patientId, page, size);
    }

    public AppointmentDto assignPatientToAppointment(Long appointmentId, Long patientId) {
        return medicalclinicClient.assignPatientToAppointment(appointmentId, patientId);
    }
}
