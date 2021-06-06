package com.example.demo.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Employee;
import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.RegisterRequest;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.security.JWTUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

	AuthenticationManager authenticationManager;
	EmployeeRepository employeeRepository;
	RoleRepository roleRepository;
	PasswordEncoder passwordEncoder;
	JWTUtils jwtUtils;
	
	@Autowired
	public AuthController(AuthenticationManager authenticationManager, EmployeeRepository employeeRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
		super();
		this.authenticationManager = authenticationManager;
		this.employeeRepository = employeeRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		System.out.println("inside login");
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		System.out.println("after auth");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwtToken = jwtUtils.generateJwtToken(authentication);
		
		return ResponseEntity.ok(jwtToken);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
		if(employeeRepository.existsByUsername(registerRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body("Username is already taken!");
		}
		if(employeeRepository.existsByEmail(registerRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body("User with this email already exists!");
		}

		
		Employee newEmployee = new Employee();
		newEmployee.setUsername(registerRequest.getUsername());
		newEmployee.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		newEmployee.setFirstName(registerRequest.getFirstName());
		newEmployee.setLastName(registerRequest.getLastName());
		newEmployee.setSalary(registerRequest.getSalary());
		newEmployee.setEmail(registerRequest.getEmail());
		newEmployee.setDepartment(registerRequest.getDepartment());
		newEmployee.setDesignation(registerRequest.getDesignation());
		
		String roleString = registerRequest.getRole();
		Set<Role> roles = new HashSet<>();
			switch (roleString) {
			case "ADMIN":
				roles.add(new Role(RoleEnum.ADMIN));
			default: 
				roles.add(new Role(RoleEnum.EMPLOYEE));
				break;
			}
		newEmployee.setRoles(roles);
		employeeRepository.save(newEmployee);
		return ResponseEntity.ok("User registered successfully!");
	}
}
