package app.controller;

import app.aspect.TrackExecutionTime;
import app.model.User;
import app.service.CosmosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Cosmos Controller", description = "the Cosmos Controller API with documentation annotations")
@RequestMapping("/api/data")
@TrackExecutionTime
public class SeedController {

    private final CosmosService service;

    public SeedController(CosmosService service) {
        this.service = service;
    }


    @Operation(summary = "Generate random test data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "generated data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid count", content = @Content),
            @ApiResponse(responseCode = "404", description = "Foo not found", content = @Content)})
    @GetMapping("/generate")
    public String generate(@RequestParam int count) {

        service.generateData(count);
        return "Generated " + count + " users with cars!";
    }

    @Operation(summary = "Deletes everything in cosmos")
    @DeleteMapping("/delete")
    public void deleteAll() {
        service.deleteAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get a user by id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/getUser")
    public Optional<User> getUser(@RequestParam String id) {
        return service.GetUser(id);
    }

    @GetMapping("/getUserByIdAndTenant")
    public Optional<User> getUserByIdAndTenant(@RequestParam String id, @RequestParam String tenant) {
        return service.GetUser(id, tenant);
    }
}
