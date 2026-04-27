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
        var id = "fbe08d93-ce03-47ee-9d15-8b97856480fb";
        String tenant = "B";

        Optional<User> user = cosmosService.GetUser(id, tenant);
        Assertions.assertTrue(user.isPresent(), "User should not be null");
    }

    @Test
    public void testGetAllByTenantId() {
        String tenant = "B";

        List<User> user = cosmosService.findAll(tenant);
        Assertions.assertTrue(!user.isEmpty(), "Users should not be empty");
    }

    @Test
    public void testSaveAndGetUser() {
        User user = cosmosService.saveOne();

        Optional<User> userContainer = cosmosService.GetUser(user.getId(), user.getTenantId());
        Assertions.assertTrue(userContainer.isPresent(), "User should not be null");

        User receivedUser = userContainer.get();
        Assertions.assertEquals(user.getId(), receivedUser.getId());
        Assertions.assertEquals(user.getTenantId(), receivedUser.getTenantId());

    }
}
