package app.service;

import app.model.Car;
import app.model.User;
import app.repository.CarRepository;
import app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class SeedService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CarRepository carRepo;
    private final Random random = new SecureRandom();

    private final Logger logger = LoggerFactory.getLogger(SeedService.class);

    private final List<String> names = List.of("Alice", "Bob", "Charlie", "David");
    private final List<String> brands = List.of("Toyota", "BMW", "Ford", "Tesla");

    public void generateData(int count) {

        logger.info("Seed called with count:" + count);

        for (int i = 0; i < count; i++) {

            String userId = UUID.randomUUID().toString();
            User user = new User(
                    userId,
                    names.get(random.nextInt(names.size())),
                    random.nextInt(60) + 18
            );

            userRepo.save(user);

            // each user gets 1-3 cars
            int carCount = random.nextInt(3) + 1;

            for (int j = 0; j < carCount; j++) {
                Car car = new Car(
                        UUID.randomUUID().toString(),
                        brands.get(random.nextInt(brands.size())),
                        "Model-" + random.nextInt(100),
                        random.nextInt(20),
                        userId
                );

                carRepo.save(car);
            }
        }
    }

    public void deleteAll() {
        carRepo.deleteAll();
        userRepo.deleteAll();
    }
}
