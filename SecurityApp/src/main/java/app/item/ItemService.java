package app.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final AtomicLong ids = new AtomicLong();
    private final ConcurrentMap<Long, Item> items = new ConcurrentHashMap<>();

    public List<Item> findAll() {
        return items.values().stream().sorted((left, right) -> Long.compare(left.id(), right.id())).toList();
    }

    public Item findById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return item;
    }

    public Item create(ItemRequest request) {
        long id = ids.incrementAndGet();
        Item item = new Item(id, request.name(), request.description());
        items.put(id, item);
        return item;
    }

    public Item update(long id, ItemRequest request) {
        findById(id);
        Item item = new Item(id, request.name(), request.description());
        items.put(id, item);
        return item;
    }

    public void delete(long id) {
        if (items.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
    }
}
