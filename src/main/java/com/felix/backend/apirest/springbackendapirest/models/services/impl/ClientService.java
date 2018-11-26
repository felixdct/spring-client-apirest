package com.felix.backend.apirest.springbackendapirest.models.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.felix.backend.apirest.springbackendapirest.models.dao.IClientDao;
import com.felix.backend.apirest.springbackendapirest.models.entity.Client;
import com.felix.backend.apirest.springbackendapirest.models.services.IClientService;

@Service
public class ClientService implements IClientService {

	@Autowired
	private IClientDao clientDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Client> findAll(Pageable pageable) {
		return clientDao.findAll(pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public Client findById(Long id) {
		// TODO Auto-generated method stub
		return clientDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public Client save(Client client) {
		// TODO Auto-generated method stub
		return clientDao.save(client);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		clientDao.deleteById(id);
	}

}
