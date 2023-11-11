package app.netlify.clementgombauld.banking.account.rest.account.out;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

}
