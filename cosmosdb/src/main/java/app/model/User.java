package app.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;

@Container(containerName = "users")
@Data
@AllArgsConstructor
public class User {

    @Id
    private String id;
    @PartitionKey
    private String tenantId;   // 🔥 partition key (important for scaling)
    private String name;
    private int age;
    private List<Car> cars;
    private int score;

    private Instant createdAt;
}
