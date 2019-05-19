package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.khaminfo.ppmtool.domain.Question;

@Repository
public interface QuestionRepository  extends CrudRepository<Question, Long>{

}
