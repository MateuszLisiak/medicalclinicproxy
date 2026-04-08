package medicalclinicproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.client.DoctorClient;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorClient doctorClient;

    public PageableContentDto<DoctorDto> getDoctors(String specialization, int page, int size) {
        log.info("Fetching doctors - specialization: {}", specialization);
        return doctorClient.getDoctors(specialization, page, size);
    }
}