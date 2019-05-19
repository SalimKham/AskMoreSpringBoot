package io.khaminfo.ppmtool.services;

import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.khaminfo.ppmtool.domain.Student;
import io.khaminfo.ppmtool.domain.Teacher;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.domain.UserInfo;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.ProfileRepository;
import io.khaminfo.ppmtool.repositories.StudentRepository;
import io.khaminfo.ppmtool.repositories.TeacherRepository;
import io.khaminfo.ppmtool.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ProfileRepository profileRepository;
	
	public User saveUser(User newUser , int type) {
		
		try {
		  newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
		  UserInfo info = new UserInfo();
		  info.setUser(newUser);
		  profileRepository.save(info);
		  newUser.setUser_state(5);
		  String s = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		  String confirm_Code="";
		  Random r = new Random ();
		  for (int i = 0; i < 20; i++) {
		   confirm_Code+=s.charAt(r.nextInt(62));	
		  }
		newUser.setConfirmPassword(confirm_Code);
		newUser.setType(type);
		 if(type == 2)
			 return studentRepository.save((Student)newUser);
		 else
			 return teacherRepository.save((Teacher) newUser);
		}catch(Exception e) {
			newUser = userRepository.findByUsername(newUser.getUsername());
			
				if(newUser != null ) {
					switch(newUser.getUser_state()) {
					case 5 :
						 throw new AccessException("UserName" +newUser.getUsername()+ " Exists But Not Confirmed");
					case 3:
						 throw new AccessException("UserName" +newUser.getUsername()+ " Exists But Not Accepted by Admin");
				    default :
				    	throw new AccessException("userName '"+newUser.getUsername()+"' Already Exists");
					}
				}
				 
		}
		return null;	
	}
	
	public Iterable<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public void ActivateUser(long id, Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		
		if(userRepository.updateUserState(id, 1) != 1)
			throw new AccessException("No User Found");
		
	}
	public int confirmUser(long id , String code ) {
		User user = userRepository.findByIdAndConfirmPassword(id, code);
		if(user != null) {
			return userRepository.updateUserConirmation("", id);		
		}
		throw new AccessException("Something went Wrong!!!");
	}

	public void logoutUser(Principal principal) {
		userRepository.updateVisitDate(new Date(), principal.getName());
		
	}
	
	public User getUserById(long id) {
		User user =  userRepository.getById(id);
		if(user == null) {
			throw new AccessException("Ops!!! Profile Not found!!");
		}
		return user;
	}

	public void blockUnblock(long id, Principal principal) {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		user = userRepository.getById(id);
		int newState = 1;
		if(user.getUser_state() != 3 )
			newState = 3;
		
	
		
		if(userRepository.updateUserState(id, newState) != 1)
			throw new AccessException("No User Found");
	}
	
	public void deleteUser(long id, Principal principal) {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(principal.getName());
		if(user.getType()!=1) {
			throw new AccessException("Access Denied!!!!");
		}
		System.out.println("user id "+id);
	   userRepository.deleteById(id);	
	}

}
