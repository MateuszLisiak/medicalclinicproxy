package medicalclinicproxy.model.dto;

import java.util.List;

public record PageableContentDto<T>(
        long totalElements,
        int totalPages,
        int currentPage,
        List<T> content
) {
}