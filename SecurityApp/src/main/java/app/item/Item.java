package app.item;

import io.swagger.v3.oas.annotations.media.Schema;

public record Item(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,
        String name,
        String description) {
}
