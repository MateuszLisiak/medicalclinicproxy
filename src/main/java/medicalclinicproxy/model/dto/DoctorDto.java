package medicalclinicproxy.model.dto;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}