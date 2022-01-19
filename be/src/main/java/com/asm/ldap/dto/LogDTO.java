package com.asm.ldap.dto;

import lombok.Data;

@Data
public class LogDTO {
	private String date;
	private String time;
	private String action;
	private String description;
	private String admin_uid;
	
}
