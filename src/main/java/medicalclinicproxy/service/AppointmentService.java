package medicalclinicproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.client.AppointmentClient;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentClient appointmentClient;

    public PageableContentDto<AppointmentDto> getAll(String specialization, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        log.info("Fetching appointments");
        return appointmentClient.getAll(specialization, startDate, endDate, page, size);
    }

    public PageableContentDto<AppointmentDto> getAllAppointmentsByPatientId(Long patientId, int page, int size) {
        log.info("Fetching appointments for patientId: {}", patientId);
        return appointmentClient.getAllByPatientId(patientId, page, size);
    }

    public PageableContentDto<AppointmentDto> getAllAppointmentsByDoctorId(Long doctorId, int page, int size) {
        log.info("Fetching appointments for doctorId: {}", doctorId);
        return appointmentClient.getAllByDoctorId(doctorId, page, size);
    }

    public AppointmentDto assignPatientToAppointment(Long appointmentId, Long patientId) {
        log.info("Assigning patientId: {} to appointmentId: {}", patientId, appointmentId);
        return appointmentClient.assignPatientToAppointment(appointmentId, patientId);
    }

    public PageableContentDto<AppointmentDto> getAvailableAppointmentsByDoctorId(Long doctorId, int page, int size) {
        log.info("Fetching available appointments for doctorId: {}", doctorId);
        return appointmentClient.getAvailableAppointmentsByDoctorId(doctorId, page, size);
    }

    public PageableContentDto<AppointmentDto> getAvailableAppointments(
            String specialization, LocalDate date, LocalDateTime startDate, LocalDateTime endDate, int page, int size
    ) {
        log.info("Fetching available appointments");
        return appointmentClient.getAvailableAppointments(specialization, date, startDate, endDate, page, size);
    }

    public void deleteAppointment(Long appointmentId) {
        log.info("Deleting appointment for appointmentId: {}", appointmentId);
        appointmentClient.deleteAppointment(appointmentId);
    }
}
