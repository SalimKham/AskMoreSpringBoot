package io.khaminfo.ppmtool.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.khaminfo.ppmtool.domain.Question;
import io.khaminfo.ppmtool.domain.Questionnary;
import io.khaminfo.ppmtool.domain.Response;
import io.khaminfo.ppmtool.domain.Teacher;
import io.khaminfo.ppmtool.domain.Tutorial;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.QuestionRepository;
import io.khaminfo.ppmtool.repositories.QuestionnaryRepository;
import io.khaminfo.ppmtool.repositories.ResponseRepository;
import io.khaminfo.ppmtool.repositories.SubjectRepository;
import io.khaminfo.ppmtool.repositories.TutorialRepository;

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
	
	
	public Tutorial addTutorial(long subject , Tutorial tutorial ,MultipartFile file  ) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() != 3) {
			throw new AccessException("Access Denied!!!!");
		}
		try {
             if(file != null) {
            	 File f = new File("src/main/resources/static"+"/pdfs/");
 	        	if(!f.exists())
 	        		f.mkdir();
 	            byte[] bytes = file.getBytes();
 	            String name = ImageUtils.getRandomName();
 	           String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
 	           Path path = Paths.get("src/main/resources/static"+"/pdfs/" + name+"."+extension);
 	            Files.write(path, bytes);
 	            tutorial.setContent("pdf/"+name+"."+extension);
 	            
 	            
            	 
             }
             
             tutorial.setTeacher((Teacher) user);
	         tutorial.setSubject(subjectRepository.getById(subject));
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

}

