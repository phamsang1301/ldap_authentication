package com.asm.ldap.security;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;

import com.asm.ldap.entity.Group;
import com.asm.ldap.mapper.GroupAttributeMapper;

@Component
public class RoleUtil {
	public static final String BASE_DN = "dc=asm,dc=com";
	private static LdapTemplate ldapTemplate;
	private static GroupAttributeMapper groupAttributeMapper;

	@Autowired
	public void setLdapTemplate(LdapTemplate ldapTemplate){
		RoleUtil.ldapTemplate = ldapTemplate;
	}

	@Autowired
	public void setGroupAttributeMapper(GroupAttributeMapper groupAttributeMapper){
		RoleUtil.groupAttributeMapper = groupAttributeMapper;
	}

	public static List<Group> getGroups() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<Group> groups = ldapTemplate.search(query().where("objectClass").is("groupOfUniqueNames"),
				groupAttributeMapper
		);
		return groups;
	}

	public static Group getGroup(String groupCn) throws IndexOutOfBoundsException {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<Group> groups = ldapTemplate.search(query()
						.where("entryDn").is("cn=" + groupCn + ",ou=groups," + BASE_DN),
				groupAttributeMapper
		);
		return groups.get(0);
	}

	public static List<String> getRolesByUid(String uid) {
		List<Group> groups = getGroups();
		List<String> roles = new ArrayList<>();
		for (Group group : groups) {
			if (group.getUids().contains(uid)) {
				roles.add(group.getCn());
			}
		}
		return roles;
	}

	public static String getRolesStrByUid(String uid) {
		List<String> roles = getRolesByUid(uid);
		String rolesStr = "";
		int count = 0;
		for (String role : roles) {
			count++;
			rolesStr += (count == 1 ? role : "," + role);
		}
		return rolesStr;
	}

	private static Name buildGroupDn(String groupCn) {
		return LdapNameBuilder.newInstance().add("ou", "groups").add("cn", groupCn).build();
	}

	public static void addUserToGroup(Name personDn, String groupCn) {
		Name groupDn = buildGroupDn(groupCn);
		DirContextOperations ctx = ldapTemplate.lookupContext(groupDn);
		ctx.addAttributeValue("uniqueMember", personDn);
		ldapTemplate.modifyAttributes(ctx);
	}

	public static void removeUserFromGroup(Name personDn, String groupCn) {
		Name groupDn = buildGroupDn(groupCn);
		DirContextOperations ctx = ldapTemplate.lookupContext(groupDn);
		ctx.removeAttributeValue("uniqueMember", personDn);
		ldapTemplate.modifyAttributes(ctx);
	}
}