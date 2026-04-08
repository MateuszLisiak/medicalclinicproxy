package medicalclinicproxy.configuration;


import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.client.DoctorClient;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DoctorClientFallbackFactory implements FallbackFactory<DoctorClient> {

    @Override
    public DoctorClient create(Throwable cause) {
        return new DoctorClient() {
            @Override
            public PageableContentDto<DoctorDto> getDoctors(String specialization, int page, int size) {
                log.error("Failed to fetch doctors by specialization: {}. Reason: {}", specialization, cause.getMessage());

                throw new MedicalclinicException(
                        "Doctor service is currently unavailable. Could not retrieve specialization list.",
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }
        };
    }
}