package app.repository;

import app.model.Car;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends CosmosRepository<Car, String> {
}
