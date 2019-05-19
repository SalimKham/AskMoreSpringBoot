package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.khaminfo.ppmtool.domain.Response;
@Repository
public interface ResponseRepository extends CrudRepository<Response, Long>{

}
