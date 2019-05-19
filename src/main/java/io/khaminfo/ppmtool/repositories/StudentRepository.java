package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;

import io.khaminfo.ppmtool.domain.Student;

public interface StudentRepository extends CrudRepository<Student, Long> {

}
