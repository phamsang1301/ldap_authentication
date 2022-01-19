package com.asm.ldap.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Group {
	private String cn;
	private List<String> uids;
	
	public void addUid(String uid) {
		if (this.uids == null) {
			this.uids = new ArrayList<>();
		}
		this.uids.add(uid);
	}
}
