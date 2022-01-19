package com.asm.ldap.repo;

import java.sql.Timestamp;
import java.util.List;

import com.asm.ldap.dto.LogDTO;
import com.asm.ldap.entity.LogEntity;

public interface LogService {

	List<LogDTO> getAllLogs();
	LogEntity addLog(LogEntity log);
	LogEntity addLog(Timestamp time, String action, String description);
	LogEntity addLog(Timestamp time, String action, String description, String adminUid);
}
