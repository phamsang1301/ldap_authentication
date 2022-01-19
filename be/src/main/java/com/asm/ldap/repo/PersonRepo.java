package com.asm.ldap.repo;

import java.util.List;

import com.asm.ldap.entity.Person;

public interface PersonRepo {
	public List<Person> retrieve();
	public Person retrieveByUid(String uid);
    public String create(Person p);
    public String update(Person p);
    public String remove(String uid);
}
