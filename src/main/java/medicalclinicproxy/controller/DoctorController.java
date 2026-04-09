package medicalclinicproxy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.model.dto.DoctorDto;
import medicalclinicproxy.model.dto.PageableContentDto;
import medicalclinicproxy.service.DoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping()
    public PageableContentDto<DoctorDto> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Received GET request for doctors - specialization: '{}'", specialization);
        PageableContentDto<DoctorDto> result = doctorService.getDoctors(specialization, page, size);
        log.info("Returning {} doctors - specialization: '{}'",
                result.content().size(), specialization);
        return result;
    }
}