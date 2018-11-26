package com.felix.backend.apirest.springbackendapirest.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.felix.backend.apirest.springbackendapirest.models.entity.Client;

public interface IClientDao extends JpaRepository<Client, Long> {
	
}
