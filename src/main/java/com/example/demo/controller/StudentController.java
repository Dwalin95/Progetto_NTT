package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {



    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @GetMapping(value = "/ciao")
    public String hello() {
        return "hello World!";
    }

    @GetMapping(value = "/list")
    public Optional<List<Student>> findAll() {
        return Optional.of(studentRepository.findAll());
    }






    @DeleteMapping(value = "delete/{id}")
    public void deleteStudent(@PathVariable String id) {
        studentRepository.deleteById(id);
    }

    @PostMapping(value = "/creazione")
    public void createStudent(@RequestBody Student student) {
        studentRepository.save(student);
//        studentRepository.insert(student);
    }
}


