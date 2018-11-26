package com.felix.backend.apirest.springbackendapirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="clients")
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "can not be empty")
	@Size(min=4, max=20, message = "most to be between 4 and 20 characters")
	@Column(nullable=false)
	private String name;
	
	@NotEmpty(message = "can not be empty")
	private String lastName;
	
	@NotEmpty(message = "can not be empty")
	@Email(message="most to be a well-formed email")
	@Column(nullable=false, unique=true)
	private String email;
	
	@NotNull(message = "can not be empty")
	@Temporal(TemporalType.DATE)
	private Date date;
	
	private String photo;
	
//	@PrePersist
//	public void prePersist() {
//		this.date = new Date();
//	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date data) {
		this.date = data;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
