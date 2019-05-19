package io.khaminfo.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.khaminfo.ppmtool.domain.Comment;
import io.khaminfo.ppmtool.domain.Field;
import io.khaminfo.ppmtool.domain.LearningEvent;
import io.khaminfo.ppmtool.domain.Tutorial;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.CommentRespository;
import io.khaminfo.ppmtool.repositories.LearningEventRepository;
import io.khaminfo.ppmtool.repositories.TutorialRepository;

@Service
public class CommentService {
	@Autowired
	private CommentRespository commentRepository;
	@Autowired
	private LearningEventRepository learningEventRepository;
	@Autowired
	private TutorialRepository tutorialRepository;
	public Comment addComment( long id_parent,Comment comment) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    LearningEvent parent  = learningEventRepository.getById(id_parent);
	    if(parent == null)
	    	throw new AccessException("Some Thing Wents Wrong!");
		  comment.setUser(user);
		
			  tutorialRepository.updateNbrComments(parent.getId());
		
		  comment.setParent(parent);
		  try {
		  return commentRepository.save(comment);
		  }catch (Exception e) {
			  throw new AccessException("Some Thing Wents Wrong!");
		}
	}
	
	public Iterable<Comment> getAll(long idEvent) {
		LearningEvent parent  = learningEventRepository.getById(idEvent);
		return parent.getComments();
	}

	public void  delete(long id) {
		commentRepository.deleteById(id);
	}


}
