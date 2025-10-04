package app.repository;

import app.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll(); // clean Redis before each test
    }

    @Test
    void testSaveAndFindStudent() {
        // given
        Student student = new Student("1", "Alice", Student.Gender.FEMALE, 10);

        // when
        studentRepository.save(student);
        Student found = studentRepository.findById("1").orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getGender()).isEqualTo(Student.Gender.FEMALE);
        assertThat(found.getGrade()).isEqualTo(10);
    }

    @Test
    void testUpdateStudent() {
        // given
        Student student = new Student("2", "Bob", Student.Gender.MALE, 8);
        studentRepository.save(student);

        // when
        student.setGrade(9);
        studentRepository.save(student);

        // then
        Student updated = studentRepository.findById("2").orElseThrow();
        assertThat(updated.getGrade()).isEqualTo(9);
    }

    @Test
    void testDeleteStudent() {
        // given
        Student student = new Student("3", "Charlie", Student.Gender.MALE, 11);
        studentRepository.save(student);

        // when
        studentRepository.deleteById("3");

        // then
        assertThat(studentRepository.findById("3")).isEmpty();
    }

    @Test
    void testCountAndFindAll() {
        // given
        studentRepository.save(new Student("4", "Dana", Student.Gender.FEMALE, 12));
        studentRepository.save(new Student("5", "Eli", Student.Gender.MALE, 9));

        // when
        Iterable<Student> students = studentRepository.findAll();

        // then
        assertThat(students).hasSize(2);
        assertThat(studentRepository.count()).isEqualTo(2);
    }
}