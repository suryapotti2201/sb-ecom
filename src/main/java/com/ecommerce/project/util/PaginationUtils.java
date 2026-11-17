package com.ecommerce.project.util;

import java.util.Set;

import com.ecommerce.project.exception.EcommerceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public final class PaginationUtils {

    public static Pageable createPageRequest(int pageNumber, int pageSize, String sortBy,
            String sortOrder, Set<String> allowedSortFields) {

        if (pageNumber < 0) {
            throw new EcommerceException("pageNumber must be 0 or greater.");
        }

        if (pageSize < 1) {
            throw new EcommerceException("pageSize must be greater than 0.");
        }

        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            throw new EcommerceException(
                    "Invalid sortBy. Allowed values: "
                            + String.join(", ", new java.util.TreeSet<>(allowedSortFields)) + "."
            );
        }

        Sort.Direction direction = Sort.Direction.fromOptionalString(sortOrder)
                .orElseThrow(() -> new EcommerceException("sortOrder must be asc or desc."));

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
    }
}