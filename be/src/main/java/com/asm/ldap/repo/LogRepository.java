package com.asm.ldap.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asm.ldap.entity.LogEntity;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
	
	@Query(value = "SELECT *"
			+ " FROM log"
			+ " ORDER BY time DESC", nativeQuery = true)
	List<LogEntity> getAllLogsEntries();
}
