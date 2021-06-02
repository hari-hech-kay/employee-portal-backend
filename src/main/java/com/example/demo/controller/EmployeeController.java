package com.example.demo.controller;

import com.example.demo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.demo.repository.EmployeeRepository;

import java.util.*;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins="https://employee-potal.herokuapp.com")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@GetMapping("/employees")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	}
	
	@GetMapping("/employees/{id}")
	@PreAuthorize("hasRole('EMPLOYEE')  or hasRole('ADMIN')")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") Long employeeId)
		throws ResourceNotFoundException {
			Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new ResourceNotFoundException("Employee not found for this id: " + employeeId));
			return ResponseEntity.ok().body(employee);
			}
	
	@PostMapping("/employees")
	@PreAuthorize("hasRole('ADMIN')")
	public Employee createEmployee(@RequestBody Employee employee) {
		return employeeRepository.save(employee);
	}
	
	@PutMapping("/employees/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Employee> updateEmployee(@PathVariable(value = "id") Long employeeId,  @Validated @RequestBody Employee employeeDetails) throws ResourceNotFoundException {
		Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new ResourceNotFoundException("Employee not found for this id: " + employeeId));
		
		employee.setFirstName(employeeDetails.getFirstName());
		employee.setLastName(employeeDetails.getLastName());
		employee.setEmail(employeeDetails.getEmail());
		employee.setDepartment(employeeDetails.getDepartment());
		employee.setDesignation(employeeDetails.getDesignation());
		employee.setSalary(employeeDetails.getSalary());	
		
		final Employee updatedEmployee = employeeRepository.save(employee);
		return ResponseEntity.ok().body(updatedEmployee);
		
	}
	
	 @DeleteMapping("/employees/{id}")
	 @PreAuthorize("hasRole('ADMIN')")
	    public Map<String, Boolean> deleteEmployee(@PathVariable(value = "id") Long employeeId)
	         throws ResourceNotFoundException {
	        Employee employee = employeeRepository.findById(employeeId)
	       .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

	        employeeRepository.delete(employee);
	        Map<String, Boolean> response = new HashMap<>();
	        response.put("deleted", true);
	        return response;
}
}









