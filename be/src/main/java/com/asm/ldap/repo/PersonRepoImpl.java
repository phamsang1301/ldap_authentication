package com.asm.ldap.repo;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.asm.ldap.entity.Person;
import com.asm.ldap.mapper.PersonAttributeMapper;
import com.asm.ldap.security.RoleUtil;

@CrossOrigin
@Repository
public class PersonRepoImpl implements PersonRepo {
	public static final String BASE_DN = "dc=asm,dc=com";
	public static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	Authentication authentication;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LogService logService;
    
    @Autowired
    private PersonAttributeMapper personAttributeMapper;

    @Override
    public String create(Person p) {
        Name personDn = buildPersonDn(p.getUid());
        authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            ldapTemplate.bind(personDn, null, buildAttributes(p));
            logService.addLog(null, "Create", "User ID:" + p.getUid(),authentication.getName());

        } catch (Exception e)
        {
            logService.addLog(null, "Create", "User ID:" + p.getUid() + " - failed",authentication.getName());
            return null;
        }
        Name dnForAddUserToGroup = buildPersonDnWithBaseDn(p.getUid());

        RoleUtil.addUserToGroup(dnForAddUserToGroup, "user");
        return p.getUid();
    }

    @Override
    public String update(Person p) {
        Name dn = buildPersonDn(p.getUid());
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logService.addLog(null, "Update", "User ID:" + p.getUid(),authentication.getName());
        ldapTemplate.rebind(dn, null, buildAttributes(p));
        return p.getUid();
    }

    @Override
    public String remove(String uid) {
        Name personDn = buildPersonDn(uid);
        // ldapTemplate.unbind(dn, true); //Remove recursively all entries
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logService.addLog(null, "Delete", "User ID:" + uid, authentication.getName());
        ldapTemplate.unbind(personDn);
        Name dnForRemoveUserFromGroup = buildPersonDnWithBaseDn(uid);
        RoleUtil.removeUserFromGroup(dnForRemoveUserFromGroup, "user");
        return uid;
    }

    @Override
    public List<Person> retrieve() {
    	List<String> uids = RoleUtil.getGroup("user").getUids();
    	List<String> adminIds = RoleUtil.getGroup("admin").getUids();
    	List<String> userIds = ListUtils.subtract(uids, adminIds);
        List<Person> people = new ArrayList<>();
    	for (String uid : userIds) {
    		people.add(retrieveByUid(uid));
    	}
        return people;
    }

    @Override
	public Person retrieveByUid(String uid) throws IndexOutOfBoundsException {
    	SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        List<Person> people = ldapTemplate.search(query()
    		.where("entryDn").is("uid=" + uid + ",ou=people," + BASE_DN),
    		personAttributeMapper);
        return people.get(0);
	}

    private Attributes buildAttributes(Person p) {
        BasicAttribute ocattr = new BasicAttribute("objectClass");
        ocattr.add("top");
        ocattr.add("person");
        ocattr.add("organizationalPerson");
        ocattr.add("inetOrgPerson");
        ocattr.add("customPerson");
        Attributes attrs = new BasicAttributes();
        attrs.put(ocattr);
        attrs.put("uid", p.getUid());
    	attrs.put("userPassword", bCryptPasswordEncoder.encode(p.getUserPassword()));
        attrs.put("cn", p.getCn());
        attrs.put("givenName", p.getGivenName());
        attrs.put("sn", p.getSn());
        attrs.put("gender", p.getGender());
        attrs.put("mail", p.getMail());
        return attrs;
    }

    private Name buildPersonDn(String uid) {
        return LdapNameBuilder.newInstance().add("ou", "people").add("uid", uid).build();
    }
    private Name buildPersonDnWithBaseDn(String uid){
        return LdapNameBuilder.newInstance(BASE_DN).add("ou", "people").add("uid", uid).build();
    }
//    private Name buildBaseDn() {
//        return LdapNameBuilder.newInstance(BASE_DN).add("ou", "people").build();
//    }
}
