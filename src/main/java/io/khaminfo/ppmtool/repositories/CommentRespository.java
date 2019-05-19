package io.khaminfo.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.khaminfo.ppmtool.domain.Comment;

@Repository
public interface CommentRespository extends CrudRepository<Comment, Long> {

}
