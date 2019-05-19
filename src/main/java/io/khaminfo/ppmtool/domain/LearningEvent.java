package io.khaminfo.ppmtool.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class LearningEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotBlank(message ="title required")
	private String title;
	@NotBlank(message = "Content requried")
	private String content;
	private int contentType;
	@ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="subject_id", updatable = false, nullable = false)
	private Subject subject;
	@ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="teacher_id", updatable = false, nullable = false)
	private Teacher teacher;
	private String allowedGroupes = "";
	@OneToMany(mappedBy = "Parent", cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Comment> comments = new ArrayList<>();
	
	
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public String getAllowedGroupes() {
		return allowedGroupes;
	}
	public void setAllowedGroupes(String allowedGroupes) {
		this.allowedGroupes = allowedGroupes;
	}
	public long getId() {
		
		
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public Teacher getTeacher() {
		return teacher;
	}
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

}
