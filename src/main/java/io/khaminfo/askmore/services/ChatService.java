package io.khaminfo.askmore.services;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.khaminfo.askmore.domain.Chat;
import io.khaminfo.askmore.domain.User;
import io.khaminfo.askmore.exceptions.AccessException;
import io.khaminfo.askmore.repositories.ChatRepository;

@Service
public class ChatService {
	@Autowired
	private ChatRepository chatRepository;

	public Chat sendMessage( Chat message) {
		  
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
			throw new AccessException("Access Denied!!!!");
		}
		return chatRepository.save(message);
	}

	
	public List<Object[]> getMessages( long id){
		
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
			throw new AccessException("Access Denied!!!!");
		}
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Object[]> result =  chatRepository.getAllMessages(user.getId(), id);
		chatRepository.updateSeen(id, user.getId());
		return result;
	}
}
