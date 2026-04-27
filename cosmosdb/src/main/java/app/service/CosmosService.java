package app.service;

import app.model.Car;
import app.model.User;
import app.repository.UserRepository;
import com.azure.cosmos.models.PartitionKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class CosmosService {

    @Autowired
    private UserRepository userRepo;
    //@Autowired
    //private CarRepository carRepo;
    private final Random random = new SecureRandom();

    private final Logger logger = LoggerFactory.getLogger(CosmosService.class);

    private final List<String> names = List.of("Alice", "Bob", "Charlie", "David");
    private final List<String> brands = List.of("Toyota", "BMW", "Ford", "Tesla");
    private final List<String> partitions = List.of("A", "B", "C", "D", "E", "F");

    public User saveOne() {
        User user = createUser();
        userRepo.save(user);
        return user;
    }

    public void generateData(int count) {

        logger.info("Seed called with count:" + count);

        for (int i = 0; i < count; i++) {
            User user = createUser();
            https:
//github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/azure-spring-data-cosmos/README.md
            //userRepo.save(user.getId(), new PartitionKey(user.getTenantId()), User.class);
            userRepo.save(user);
        }
    }

    public Optional<User> GetUser(String id) {
        return userRepo.findById(id);
    }

    public Optional<User> GetUser(String id, String tenantId) {
        return userRepo.findById(id, new PartitionKey(tenantId));
    }

    public void deleteAll() {
        //carRepo.deleteAll();
        userRepo.deleteAll();
    }

    public List<User> findAll(String tenantId) {
        Iterator<User> source = userRepo.findAll(new PartitionKey(tenantId)).iterator();
        List<User> target = new ArrayList<>();
        source.forEachRemaining(target::add);
        return target;
    }

    public User createUser() {
        String userId = UUID.randomUUID().toString();
        List<Car> cars = new ArrayList<>();
        // each user gets 1-3 cars
        int carCount = random.nextInt(3) + 1;

        for (int j = 0; j < carCount; j++) {
            Car car = new Car(
                    brands.get(random.nextInt(brands.size())),
                    "Model-" + random.nextInt(100),
                    random.nextInt(20)
            );

            cars.add(car);
        }
        User user = new User(
                userId,
                partitions.get(random.nextInt(partitions.size())),
                names.get(random.nextInt(names.size())),
                random.nextInt(60) + 18,
                cars,
                random.nextInt(10),
                Instant.now()
        );
        return user;
    }
}
