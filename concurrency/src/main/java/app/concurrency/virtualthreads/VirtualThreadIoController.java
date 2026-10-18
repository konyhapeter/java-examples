package app.concurrency.virtualthreads;

import app.concurrency.IoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
class VirtualThreadIoController {

    @GetMapping("/io")
    IoResponse simulateIo(@RequestParam(defaultValue = "100") int delayMs)
            throws InterruptedException {
        validateDelay(delayMs);
        Thread.sleep(delayMs);
        return new IoResponse("virtual-threads", delayMs, Thread.currentThread().isVirtual());
    }

    private static void validateDelay(int delayMs) {
        if (delayMs < 0 || delayMs > 1_000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "delayMs must be between 0 and 1000");
        }
    }
}
