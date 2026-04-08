package medicalclinicproxy.client;

import medicalclinicproxy.configuration.DoctorClientFallbackFactory;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "medicalclinic", contextId = "doctorClient",
        url = "${spring.cloud.openfeign.client.config.medical-clinic.url}",
        fallbackFactory = DoctorClientFallbackFactory.class)
public interface DoctorClient {

    @GetMapping("/doctors")
    PageableContentDto<DoctorDto> getDoctors(
            @RequestParam("specialization") String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );
}