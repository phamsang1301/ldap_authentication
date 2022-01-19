package com.asm.ldap.mapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import com.asm.ldap.entity.Group;

@Component
public class GroupAttributeMapper implements AttributesMapper<Group>{
	@Override
	public Group mapFromAttributes(Attributes attrs) throws NamingException {
		Group group = new Group();
		if (attrs != null) {
			try {
				for (NamingEnumeration<? extends Attribute> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					
					if (attr.getID().equals("cn")) {
						group.setCn((String) attr.get());
					} else if (attr.getID().equals("uniqueMember")) {
						for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
							String dn = (String) e.next();
							String uid = dn.split(",")[0].split("=")[1];
							group.addUid(uid);
						}
					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return group;
	}
}
