package dk.ek;

public record FetchResult(
        String url,
        int statusCode,
        int responseSize,
        long durationMs,
        String threadName
) {}
