package app.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@SecurityRequirement(name = "oauth2")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List items")
    public List<Item> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an item")
    public Item findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an item")
    public Item create(@Valid @RequestBody ItemRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace an item")
    public Item update(@PathVariable long id, @Valid @RequestBody ItemRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an item")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
