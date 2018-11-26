package com.felix.backend.apirest.springbackendapirest.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.felix.backend.apirest.springbackendapirest.models.entity.Client;
import com.felix.backend.apirest.springbackendapirest.models.services.IClientService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClientRestController {
	@Autowired
	private IClientService clientService;
	
	private final Logger log = LoggerFactory.getLogger(ClientRestController.class);
	
	@GetMapping("/clients")
	public List<Client> index() {
		return clientService.findAll();
	}
	
	@GetMapping("/clients/page/{page}")
	public Page<Client> index(@PathVariable Integer page) {
		return clientService.findAll(PageRequest.of(page, 4)); // to page a web page
	}
	
	@GetMapping("/clients/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Client client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			client = clientService.findById(id);
		} catch(DataAccessException e) {
			response.put("message", "Error in the query in the database");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(client == null) {
			response.put("mensaje", "The client with the ID: "
					.concat(id.toString()).concat(" don't exist in the database"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Client>(client, HttpStatus.OK);
	}
	
	@PostMapping("/clients")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Client client, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		Client clientInside = null;
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "The field '"+ err.getField() + "' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clientInside = clientService.save(client);
		} catch(DataAccessException e) {
			response.put("mensaje", "The client  could not be created");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("succesful", "The client was created successfully");
		response.put("client", clientInside);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/clients/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
			@RequestParam("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();
		Client client = clientService.findById(id);
		
		if(!file.isEmpty()) {
			String nameFile = UUID.randomUUID().toString()+ "_" 
								+file.getOriginalFilename().replaceAll(" ", "");
			Path routeFile = Paths.get("uploads").resolve(nameFile).toAbsolutePath(); 
			log.info(routeFile.toString());
			try {
				Files.copy(file.getInputStream(), routeFile);
			} catch (IOException e) {
				response.put("mensaje", "The photo could not be uploaded correctly. Try again");
				response.put("error", e.getMessage().concat(": ")
						.concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String namePhotoBefore = client.getPhoto();
			if(namePhotoBefore != null && namePhotoBefore.length() > 0) {
				Path routeFileBefore = Paths.get("uploads").resolve(namePhotoBefore).toAbsolutePath(); 
				File photoBefore = routeFileBefore.toFile();
				
				if(photoBefore.exists() && photoBefore.canRead()) {
					photoBefore.delete();
				}
			}
			
			client.setPhoto(nameFile);
			clientService.save(client);
			response.put("client", client);
			response.put("mensaje", "The photo " + nameFile + " was uploaded correctly");
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	@GetMapping("/uploads/img/{namePhoto:.+}")
	public ResponseEntity<Resource> watchPhoto(@PathVariable String namePhoto) {
		Path routeFile = Paths.get("uploads").resolve(namePhoto).toAbsolutePath();
		log.info(routeFile.toString());
		Resource resource = null;
		
		try {
			resource = new UrlResource(routeFile.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(!resource.exists() && resource.isReadable()) {
			throw new RuntimeException("Error, can not be possible to load the photo");
		}
		
		HttpHeaders header =  new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
				+ resource.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
	@PutMapping("/clients/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Client client, BindingResult result, @PathVariable Long id) {
		Client currentClient = clientService.findById(id);
		Client clientUpdate = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "The field '"+ err.getField() + "' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(currentClient == null) {
			response.put("mensaje", "The client with the ID: "
					.concat(id.toString()).concat(" can't be edit, don't exist in the db"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			currentClient.setName(client.getName());
			currentClient.setLastName(client.getLastName());
			currentClient.setEmail(client.getEmail());
			clientUpdate = clientService.save(currentClient);
		} catch(DataAccessException e) {
			response.put("mensaje", "The client  could not be update");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("succesful", "The client was update successfully");
		response.put("client", clientUpdate);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clients/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Client client = clientService.findById(id);
			String namePhotoBefore = client.getPhoto();
			if(namePhotoBefore != null && namePhotoBefore.length() > 0) {
				Path routeFileBefore = Paths.get("uploads").resolve(namePhotoBefore).toAbsolutePath(); 
				File photoBefore = routeFileBefore.toFile();
				
				if(photoBefore.exists() && photoBefore.canRead()) {
					photoBefore.delete();
				}
			}
			clientService.delete(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "The client  could not be delete");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("succesful", "The client was delete successfully");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
