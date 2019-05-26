package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.khaminfo.ppmtool.domain.Subject;
@Repository
public interface  SubjectRepository extends CrudRepository<Subject, Long>{
	Subject getById(Long id);
}
