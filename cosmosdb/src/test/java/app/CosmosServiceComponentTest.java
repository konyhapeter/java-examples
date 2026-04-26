package app;

import app.model.User;
import app.service.CosmosService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CosmosServiceComponentTest {

    @Autowired
    private CosmosService cosmosService;

    @Test
    public void testGetUserWithPartitionKey() {
        var id = "b3cf57ff-ee10-4349-9ccd-32bbff662eb4";
        String tenant = "B";

        Optional<User> user = cosmosService.GetUser(id, tenant);
        Assertions.assertTrue(user.isPresent(), "User should not be null");
    }

    @Test
    public void testGetAllByTenantId(){
        String tenant = "B";

        List<User> user = cosmosService.findAll(tenant);
        Assertions.assertTrue(!user.isEmpty(), "Users should not be empty");
    }
}
