package app.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Container(containerName = "users")
@Data
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String name;
    private int age;
}
