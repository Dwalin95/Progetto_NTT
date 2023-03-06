package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


//nella mongo repository possiamo accedere a determinati metodi già implementati all'interno di essa
@Repository                                        //Tipo Studente    data type della pk
public interface StudentRepository extends MongoRepository<Student, String> {



}
