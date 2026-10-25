package app.wikimedia;

import java.time.Instant;

public record WikimediaRecentChange(
        String eventId,
        String wiki,
        String title,
        String changeType,
        String user,
        Instant occurredAt,
        String pageUrl,
        Long revisionId) {
}
