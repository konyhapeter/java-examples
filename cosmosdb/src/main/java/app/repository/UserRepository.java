package app.repository;

import app.model.User;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CosmosRepository<User, String> {


    /*[!IMPORTANT]
    Cross-Partition Warning: Since this query does not include tenantId in the WHERE clause,
    Cosmos DB will perform a Cross-Partition Query. This is fine for small datasets,
    but as your "users" container grows,
    this will become expensive in terms of Request Units (RUs).
    */
    @Query("SELECT * FROM c WHERE ARRAY_LENGTH(c.cars) > 1")
    List<User> findUsersWithMultipleCars();
}
