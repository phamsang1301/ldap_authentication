package com.asm.ldap.repo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.asm.ldap.dto.LogDTO;
import com.asm.ldap.entity.LogEntity;

@Service
public class LogServiceImpl implements LogService {
	
	Authentication authentication;

	@Autowired
	LogRepository logRepo;
	
	@Override
	public List<LogDTO> getAllLogs() {
		
		List<LogEntity> logList =  logRepo.getAllLogsEntries();
		return logList.stream().map(e-> getLogDto(e)).collect(Collectors.toList());
	}
	@Override
	public LogEntity addLog(LogEntity log) {
		LogEntity result = null;
		if (!Objects.isNull(log)) {
			result =  logRepo.save(log);
		}
		return result;
	}
	@Override
	public LogEntity addLog(Timestamp time, String action, String description) {
		authentication = SecurityContextHolder.getContext().getAuthentication();
		LogEntity log = new LogEntity();
		if(Objects.isNull(time))
			time = Timestamp.from(Instant.now());
		log.setTime(time);
		log.setAction(action);
		log.setDescription(description);

		return addLog(log);
	}
	
	@Override
	public LogEntity addLog(Timestamp time, String action, String description, String adminUid) {
		LogEntity log = new LogEntity();
		if(Objects.isNull(time))
			time = Timestamp.from(Instant.now());
		log.setTime(time);
		log.setAction(action);
		log.setDescription(description);
		log.setAdminUId(adminUid);
		return addLog(log);
	}
	
	
	private LogDTO getLogDto(LogEntity entity)
	{
		LogDTO dto = new LogDTO();
		Timestamp time = entity.getTime();
		dto.setDate(time.toLocalDateTime().toLocalDate().toString());
		dto.setTime(time.toLocalDateTime().toLocalTime().toString());
		dto.setAction(entity.getAction());
		dto.setDescription(entity.getDescription());
		dto.setAdmin_uid(entity.getAdminUId());
		
		return dto;
	}

}
