package com.example.demo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Employee;
import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.payload.request.UpdateRequest;
import com.example.demo.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins="*")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@GetMapping("/employees")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	}
	
	@GetMapping("/employees/{id}")
	@PreAuthorize(" hasRole('EMPLOYEE') or hasRole('ADMIN')")
	public Employee getEmployeeById(@PathVariable(value = "id") Long employeeId)
		throws ResourceNotFoundException {
		System.out.println("inside get emp");
			Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new ResourceNotFoundException("Employee not found for this id: " + employeeId));
			return employee;
			}
	
	@PutMapping("/employees/{id}")
	@PreAuthorize("hasRole('ADMIN') or @employeeAuthorizer.isEmployeeCurrentlyLoggedIn(authentication.principal, #employeeId)")
	public ResponseEntity<?> updateEmployee(@PathVariable(value = "id") Long employeeId, @RequestBody UpdateRequest updateRequest) throws ResourceNotFoundException {
		System.out.println(updateRequest.toString());
		Employee emp = employeeRepository.findById(employeeId)
			       .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + employeeId));

			emp.setId(employeeId);
			emp.setFirstName(updateRequest.getFirstName());
			emp.setLastName(updateRequest.getLastName());
			emp.setSalary(updateRequest.getSalary());
			emp.setEmail(updateRequest.getEmail());
			emp.setDepartment(updateRequest.getDepartment());
			emp.setDesignation(updateRequest.getDesignation());
			
//			String roleString = updateRequest.getRole();
//			Set<Role> roles = new HashSet<>();
//				switch (roleString) {
//				case "ADMIN":
//					roles.add(new Role(RoleEnum.ADMIN));
//				default: 
//					roles.add(new Role(RoleEnum.EMPLOYEE));
//					break;
//				}
//			emp.setRoles(roles);
			final Employee updatedEmployee = employeeRepository.save(emp);
			return ResponseEntity.ok(updatedEmployee);
		
	}
	
	 @DeleteMapping("/employees/{id}")
	 @PreAuthorize("hasRole('ADMIN') or @employeeAuthorizer.isEmployeeCurrentlyLoggedIn(authentication.principal, #employeeId)")
	    public ResponseEntity<Boolean> deleteEmployee(@PathVariable(value = "id") Long employeeId)
	         throws ResourceNotFoundException {
	        Employee employee = employeeRepository.findById(employeeId)
	       .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + employeeId));

	        employeeRepository.delete(employee);
	        return ResponseEntity.ok(true);
}
}









