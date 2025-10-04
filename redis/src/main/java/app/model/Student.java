package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Student")
@Data
@AllArgsConstructor
public class Student implements Serializable {
    public enum Gender {
        MALE, FEMALE
    }

    @Id
    private String id;
    private String name;
    private Gender gender;
    private int grade;
}
