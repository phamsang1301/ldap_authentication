package com.asm.ldap.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import com.asm.ldap.entity.Person;

@Component
public class PersonAttributeMapper implements AttributesMapper<Person> {    
	@Override
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = new Person();
        person.setUid(null != attributes.get("uid") ? attributes.get("uid").get().toString() : null);
        person.setUserPassword(null != attributes.get("userPassword") ? attributes.get("userPassword").get().toString() : null);
        person.setCn(null != attributes.get("cn") ? attributes.get("cn").get().toString() : null);
        person.setGivenName(null != attributes.get("givenName") ? attributes.get("givenName").get().toString() : null);
        person.setSn(null != attributes.get("sn") ? attributes.get("sn").get().toString() : null);
        person.setGender(null != attributes.get("gender") ? attributes.get("gender").get().toString() : null);
        person.setMail(null != attributes.get("mail") ? attributes.get("mail").get().toString() : null);
        return person;
    }
}
