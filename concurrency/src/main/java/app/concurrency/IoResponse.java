package app.concurrency;

public record IoResponse(String implementation, int delayMs, boolean virtualThread) {
}
