package app.controller;

import app.service.SeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class SeedController {

    private final SeedService service;

    public SeedController(SeedService service) {
        this.service = service;
    }

    @GetMapping("/generate")
    public String generate(@RequestParam int count) {

        service.generateData(count);
        return "Generated " + count + " users with cars!";
    }

    @DeleteMapping("/delete")
    public void deleteAll(){
        service.deleteAll();
    }
}
