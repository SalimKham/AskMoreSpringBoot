package io.khaminfo.ppmtool.services;

import java.security.Principal;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.khaminfo.ppmtool.domain.Field;
import io.khaminfo.ppmtool.domain.Subject;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.FieldRepository;
import io.khaminfo.ppmtool.repositories.SubjectRepository;
import io.khaminfo.ppmtool.repositories.UserRepository;

@Service
public class FieldService {
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
    private UserRepository userRepository;	
	@Autowired
	private  SubjectRepository subjectRepository;

	public Field add( Field field, Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		try {
			
			return fieldRepository.save(field);
		}catch (Exception e) {
			throw new AccessException("Please Choose Another Name");
		}
		
	   	
	}

	public Iterable<Field> getAllField() {
		return fieldRepository.findAll();
	}

	public void archive(long id, Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		Field field = fieldRepository.getById(id);
		boolean newState = !field.isIs_arrchived();
		
		
	
		
		if(fieldRepository.updateFiedState(id, newState) != 1)
			throw new AccessException("Something went Wrong!!");
		
	}

	public void delete(long id, Principal principal) {
			// TODO Auto-generated method stub
			User user = userRepository.findByUsername(principal.getName());
			if(user.getType()!=1) {
				throw new AccessException("Access Denied!!!!");
			}
		   fieldRepository.deleteById(id);	
		
	}

	public Subject addSubject(long id_field, @Valid Subject subject, Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		try {
			Field field = fieldRepository.getById(id_field);
			if(field.isIs_arrchived())
				throw new AccessException("This field is archived Please Choose Another Field");
				
			subject.setField(field);
			return subjectRepository.save(subject);
		}catch (Exception e) {
			if(e.getClass() == AccessException.class)
				throw e;
			throw new AccessException("Please Choose Another Name");
		}
	}

	public Iterable<Subject> getAllSubjects() {
		return subjectRepository.findAll();
	}

	public void deleteSubject(long id, Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
	   subjectRepository.deleteById(id);	
		
	}

}
