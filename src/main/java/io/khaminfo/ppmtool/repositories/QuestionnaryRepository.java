package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.khaminfo.ppmtool.domain.Questionnary;
@Repository
public interface QuestionnaryRepository extends CrudRepository<Questionnary, Long> {
	 Questionnary getById(long id);

}
