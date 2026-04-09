package medicalclinicproxy.configuration;


import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.client.AppointmentClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.AppointmentDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
public class AppointmentClientFallbackFactory implements FallbackFactory<AppointmentClient> {

    @Override
    public AppointmentClient create(Throwable cause) {
        return new AppointmentClient() {

            @Override
            public PageableContentDto<AppointmentDto> getAll(String specialization, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
                log.error("Failed to fetch all appointments. Reason: {}", cause.getMessage());
                throw new MedicalclinicException("Appointment service is currently unavailable. Could not retrieve list.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            @Override
            public PageableContentDto<AppointmentDto> getAllByPatientId(Long patientId, int page, int size) {
                log.error("Fallback triggered for patient {}. Cause: {}", patientId, cause.getMessage());

                if (cause instanceof MedicalclinicException) {
                    throw (MedicalclinicException) cause;
                }

                throw new MedicalclinicException(
                        "Service is currently unavailable. Could not retrieve appointments.",
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }

            @Override
            public PageableContentDto<AppointmentDto> getAllByDoctorId(Long doctorId, int page, int size) {
                log.error("Failed to fetch appointments for doctor ID: {}. Reason: {}", doctorId, cause.getMessage());
                throw new MedicalclinicException("Could not retrieve doctor's schedule. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            @Override
            public AppointmentDto assignPatientToAppointment(Long appointmentId, Long patientId) {
                log.error("Critical failure: Cannot assign patient {} to appointment {}. Reason: {}",
                        patientId, appointmentId, cause.getMessage());

                if (cause instanceof MedicalclinicException) {
                    throw (MedicalclinicException) cause;
                }

                if (cause.getCause() instanceof MedicalclinicException) {
                    throw (MedicalclinicException) cause.getCause();
                }

                throw new MedicalclinicException(
                        "Assignment failed: The appointment service is unreachable. No changes were made.",
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }

            @Override
            public PageableContentDto<AppointmentDto> getAvailableAppointmentsByDoctorId(Long doctorId, int page, int size) {
                log.error("Failed to fetch available appointments for doctor ID: {}. Reason: {}", doctorId, cause.getMessage());
                throw new MedicalclinicException("Available slots could not be loaded at this time.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            @Override
            public PageableContentDto<AppointmentDto> getAvailableAppointments(String specialization, LocalDate date, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
                log.error("Failed to fetch filtered available appointments. Reason: {}", cause.getMessage());
                throw new MedicalclinicException("Search service is temporarily unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
            }

            @Override
            public void deleteAppointment(Long appointmentId) {
                log.error("Failed to delete appointment ID: {}. Reason: {}", appointmentId, cause.getMessage());
                throw new MedicalclinicException("Deletion failed: Appointment service is not responding.", HttpStatus.SERVICE_UNAVAILABLE);
            }
        };
    }
}