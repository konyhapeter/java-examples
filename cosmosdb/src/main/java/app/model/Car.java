package app.model;

//import com.azure.spring.data.cosmos.core.mapping.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
//import org.springframework.data.annotation.Id;

//@Container(containerName = "cars")
@Data
@AllArgsConstructor
public class Car {

    private String brand;
    private String model;
    private int age;
}
