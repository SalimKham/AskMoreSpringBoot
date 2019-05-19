package io.khaminfo.ppmtool.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.khaminfo.ppmtool.domain.Groupe;
import io.khaminfo.ppmtool.domain.Student;
import io.khaminfo.ppmtool.domain.Teacher;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.GroupeRepository;
import io.khaminfo.ppmtool.repositories.TeacherRepository;
import io.khaminfo.ppmtool.repositories.UserRepository;

@Service
public class GroupeService {

	@Autowired
	private GroupeRepository groupeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	TeacherRepository teacherRepository;

	public Groupe addGroupe(@Valid Groupe groupe, Principal principal) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() != 3) {
			throw new AccessException("Access Denied!!!!");
		}
		try {

			groupe.setOwner((Teacher) user);
			return groupeRepository.save(groupe);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new AccessException("Please Choose Another Name");
		}

	}

	public Iterable<Student> getAllStudentByGroupe(long id) {

		try {

			Groupe groupe = groupeRepository.getById(id);
			if (groupe == null)
				throw new AccessException("No Groupe Found");
			return groupe.getStudents();
		} catch (Exception e) {
			throw new AccessException("SomeThingWentWrong");
		}
	}

	public Iterable<Groupe> getAllGroupes() {
		return groupeRepository.findAll();

	}

	public void deleteGroupe(long id, Principal principal) {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(principal.getName());
		boolean access = true;
		switch (user.getType()) {

		case 3:
			Groupe groupe = groupeRepository.getById(id);
			if (groupe.getOwner().getId() != user.getId())
				access = false;
			break;
		case 2:
			access = false;
			break;

		default:
			break;
		}

		if (!access)
			throw new AccessException("Access Denied!!!!");

		groupeRepository.deleteById(id);
	}

	public void acceptStudentInGroupe(long id, long id_student, Principal principal) {
		// TODO Auto-generated method stub
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() != 3) {
			Groupe groupe = groupeRepository.getById(id);
			if (groupe.getOwner().getId() != user.getId())
				throw new AccessException("Access Denied!!!!");
			String newList = groupe.getAcceptedStudents() + "/" + id;
			if (groupeRepository.updateAcceptedStudent(id, newList) != 1)
				throw new AccessException("No groupe Found");
		}

	}

	public void JoinGroupe(long id_groupe) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() == 2) {
			Groupe groupe = groupeRepository.getById(id_groupe);
			Student student = (Student) user;
			groupe.getStudents().add(student);
			groupeRepository.save(groupe);
		}
	}

	public void leaveGroupe(long id_groupe, long idStudent) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		long id = idStudent;
		if (user.getType() == 2) {
			Student student = (Student) user;
			id = student.getId();
		}

		if (id == -1)
			return;
		Groupe groupe = groupeRepository.getById(id_groupe);

		List<Student> it = groupe.getStudents();
		int index = 0;
		for (Student st : it) {
			if (st.getId() == id) {
				it.remove(index);
				break;
			}
			index++;

		}

		if (groupe.getAcceptedStudents() != null) {
			groupe.getAcceptedStudents().replace("" + id, "");
		}
		groupeRepository.save(groupe);

	}

	public void updateState(long id, int newState, Principal principal) {
		String validStates = "123";
		if (validStates.indexOf("" + newState) == -1)
			throw new AccessException("Please Use a valide stae (1 , 2 )");
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(principal.getName());
		boolean access = true;
		switch (user.getType()) {

		case 3:
			Groupe groupe = groupeRepository.getById(id);
			if (groupe.getOwner().getId() != user.getId())
				access = false;
			break;
		case 2:
			access = false;
			break;

		default:
			break;
		}

		if (!access)
			throw new AccessException("Access Denied!!!!");

		if (groupeRepository.updateGroupeState(id, newState) != 1)
			throw new AccessException("No groupe Found");
	}

	public Iterable<Groupe> getTeacherGroupes() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user.getType() == 3) {
			Teacher teacher = teacherRepository.getById(user.getId());
			List<Groupe> groupes =  teacher.getGroupes();
			List<Groupe> result = new ArrayList<>();
	        
	        for (Groupe groupe: groupes) {
	            if (groupe.getState() == 1) {
	                result.add(groupe);
	            }
	        }
			return result;
		}
		return null;
	}
}