package app.repository;

import app.model.Car;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.data.repository.NoRepositoryBean;
//import org.springframework.stereotype.Repository;

//@Repository
@NoRepositoryBean
public interface CarRepository extends CosmosRepository<Car, String> {
}
