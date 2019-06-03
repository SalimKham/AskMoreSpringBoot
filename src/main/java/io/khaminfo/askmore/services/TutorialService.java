package io.khaminfo.askmore.services;




import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import io.khaminfo.askmore.domain.Groupe;
import io.khaminfo.askmore.domain.Question;
import io.khaminfo.askmore.domain.Questionnary;
import io.khaminfo.askmore.domain.Response;
import io.khaminfo.askmore.domain.Teacher;
import io.khaminfo.askmore.domain.Tutorial;
import io.khaminfo.askmore.domain.User;
import io.khaminfo.askmore.exceptions.AccessException;
import io.khaminfo.askmore.repositories.QuestionRepository;
import io.khaminfo.askmore.repositories.QuestionnaryRepository;
import io.khaminfo.askmore.repositories.ResponseRepository;
import io.khaminfo.askmore.repositories.SubjectRepository;
import io.khaminfo.askmore.repositories.TutorialRepository;

@Service
public class TutorialService {
	@Autowired
	private TutorialRepository tutorialRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private QuestionnaryRepository questionnaryRepository;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private ResponseRepository responseRepository;
	@Autowired
	private GroupeService groupeService;
	
	
	public Tutorial addTutorial(long subject , String allowedGroupes , Tutorial tutorial ,MultipartFile file  ) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() != 3) {
			throw new AccessException("Access Denied!!!!");
		}
		try {
             if(file != null) {
            	
 	            byte[] bytes = file.getBytes();
 	            String name = ImageUtils.getRandomName();
 	         
 	           File googleFile = CreateGoogleFile.createGoogleFile("1Cm9asmUPETWfqPpzoCAjjfFPYKyOpn80", "application/pdf", name+".pdf",bytes);
 	            tutorial.setContent(googleFile.getId());
 	            
 	            
            	 
             }
             System.out.println("allowed groupes "+allowedGroupes);
             
             if(allowedGroupes.length() != 0) {
            	 Iterable<Groupe> result = new ArrayList<>();
            	 List<Groupe> list = new ArrayList<>();
            	 
            	 result = groupeService.getTeacherGroupes();
            	  for (Groupe groupe: result) {
            		  System.out.println("groupe : "+groupe.getId());
      	            if (allowedGroupes.indexOf(""+groupe.getId()) != -1) {
      	            	System.out.println("yes allowed");
      	                list.add(groupe);
      	            }
      	        }
            	  tutorial.setAllowedGroupes(list);
             }
             tutorial.setTeacher((Teacher) user);
	         tutorial.setSubject(subjectRepository.getById(subject));
	         System.out.println("done");
	         tutorialRepository.save(tutorial);
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new AccessException("Please Choose Another Name");
		}

		return null;
	}


	public Tutorial getTutorialById(long id) {
		tutorialRepository.updateNbrVisits(id);
		return tutorialRepository.getById(id);
	}
	
	public Questionnary addQuestionnary(Long id ,String [] questionsArry, String [] ResponsesArray) {
		
		Tutorial tutorial = tutorialRepository.getById(id);
		Questionnary newQuestionnary = new Questionnary();
		newQuestionnary.setTutorial(tutorial);
		newQuestionnary = questionnaryRepository.save(newQuestionnary);
		for (int i = 0; i < questionsArry.length; i++) {
			String [] question = questionsArry[i].split(":");
			Question newQuestion = new Question();
			newQuestion.setMark(Integer.parseInt(question[0]));
			newQuestion.setQuestion(question[1]);
			newQuestion.setQuestinnary(newQuestionnary);
			newQuestion = questionRepository.save(newQuestion);
			for (int j = 0; j < ResponsesArray.length; j++) {
			String [] answer = ResponsesArray[j].split(":");
			int order = 1;
			if(Integer.parseInt(answer[0]) == (i+1)) {
				Response newAnswer = new Response();
				newAnswer.setContent(answer[1]);
				newAnswer.setQuestion(newQuestion);
				newAnswer.setResponseOrder(order);
				newAnswer.setValide(Boolean.parseBoolean(answer[2]));
				responseRepository.save(newAnswer);
				order ++;
			}
			
			}
		}
		
		return newQuestionnary;
	}
	
	public void deleteQuestionnary(long id_questionnary) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() != 3) {
			throw new AccessException("Access Denied!!!!");
		}
		Questionnary questionnary = questionnaryRepository.getById(id_questionnary);
		if(questionnary != null) {
			if(questionnary.getTutorial().getTeacher().getId() == user.getId()) {
				questionnaryRepository.deleteById(id_questionnary);
			}else {
				throw new AccessException("Access Denied!!!!");
			}
		}
	}


	public Iterable<Tutorial> getAll() {
		// TODO Auto-generated method stub
		return tutorialRepository.findAll();
	}


	public void delete(long id) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tutorial tutorial = tutorialRepository.getById(id);
		if(tutorial.getTeacher().getId() != user.getId())
			throw new AccessException("Access Denied!!!!");
		tutorialRepository.deleteById(id);
		
	}

}

