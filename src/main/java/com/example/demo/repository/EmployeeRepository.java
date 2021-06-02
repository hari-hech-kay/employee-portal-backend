package com.example.demo.repository;
import com.example.demo.model.Employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Optional<Employee> findByDesignation(String designation);

	Optional<Employee> findByUsername(String username);
	Optional<Employee> findByEmail(String email);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
