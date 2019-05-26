package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;

import io.khaminfo.ppmtool.domain.Teacher;

public interface TeacherRepository extends CrudRepository<Teacher,Long>{
	Teacher getById(Long id);
	Teacher findByUsername(String username);
}
