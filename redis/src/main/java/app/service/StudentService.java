package app.service;

import app.model.Student;
import app.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Cacheable(value = "students", key = "#id")
    public Student getStudent(String id) {
        return studentRepository.findById(id).orElse(null);
    }

    @CachePut(value = "students", key = "#student.id")
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @CacheEvict(value = "students", key = "#id")
    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }
}
