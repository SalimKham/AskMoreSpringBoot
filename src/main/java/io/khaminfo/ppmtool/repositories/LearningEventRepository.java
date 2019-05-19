package io.khaminfo.ppmtool.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.khaminfo.ppmtool.domain.LearningEvent;

@Repository
public interface LearningEventRepository extends CrudRepository<LearningEvent,Long> {
	LearningEvent getById(Long id);
	  
	
}
