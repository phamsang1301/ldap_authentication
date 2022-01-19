package com.asm.ldap.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asm.ldap.dto.LogDTO;
import com.asm.ldap.entity.Person;
import com.asm.ldap.repo.LogService;
import com.asm.ldap.repo.PersonRepo;

@CrossOrigin
@RestController
public class PersonController {
	@Autowired
	private PersonRepo personRepo;
	
	@Autowired
	private LogService logService;

	@PostMapping("/admin/add-user")
	public ResponseEntity<String> bindLdapPerson(@RequestBody Person person) {
		String result = "";
		try {
			result = personRepo.create(person);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (result == null)
		{
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(result, HttpStatus.CREATED);
	}

	@PutMapping("/admin/update-user")
	public ResponseEntity<String> rebindLdapPerson(@RequestBody Person person) {
		String result = "";
		try {
			result = personRepo.update(person);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@GetMapping("/admin/users")
	public ResponseEntity<List<Person>> retrieve() {
		List<Person> people = new ArrayList<>();
		try {
			people = personRepo.retrieve();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Person>>(people, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Person>>(people, HttpStatus.OK);
	}

	@GetMapping("/user")
	public ResponseEntity<Person> retrieveByUid(@RequestParam(name = "uid") String uid) {
		Person person = new Person();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = "";

		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
		if (!username.equals(uid)) {
			return new ResponseEntity<Person>(person, HttpStatus.FORBIDDEN);
		}
		try {
			person = personRepo.retrieveByUid(uid);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Person>(person, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	}

	@DeleteMapping("/admin/delete-user")
	public ResponseEntity<String> unbindLdapPerson(@RequestParam(name = "uid") String uid) {
		String result = "";
		try {
			result = personRepo.remove(uid);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	
	@GetMapping("/admin/log")
	public ResponseEntity<List<LogDTO>> getAllLogEntries()
	{
		return new ResponseEntity<List<LogDTO>>(logService.getAllLogs(),HttpStatus.OK);
	}
	
	@GetMapping("/admin/search")
	public ResponseEntity<Person> adminRetrieveByUid(@RequestParam(name = "uid") String uid) {
		if (uid == "")
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		Person person = new Person();
		try {
			person = personRepo.retrieveByUid(uid);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Person>(person, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	}
}
