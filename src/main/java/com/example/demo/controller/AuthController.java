package com.example.demo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.model.Employee;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.RegisterRequest;
import com.example.demo.payload.response.JWTResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.security.AppUserDetails;
import com.example.demo.security.JWTUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

	AuthenticationManager authenticationManager;
	EmployeeRepository userRepository;
	RoleRepository roleRepository;
	PasswordEncoder passwordEncoder;
	JWTUtils jwtUtils;
	
	@Autowired
	public AuthController(AuthenticationManager authenticationManager, EmployeeRepository employeeRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
		super();
		this.authenticationManager = authenticationManager;
		this.userRepository = employeeRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		AppUserDetails appUserDetails = (AppUserDetails) auth.getPrincipal();
		List<String> roles = appUserDetails.getAuthorities()
				.stream()
				.map(role -> role.getAuthority())
				.collect(Collectors.toList());
		String jwtToken = jwtUtils.generateJwtToken(auth);
		
		return ResponseEntity.ok(
				new JWTResponse(
						jwtToken,
						appUserDetails.getUsername(),
						appUserDetails.getPassword(),
						roles));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
		if(userRepository.existsByUsername(registerRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Username is already taken!"));
		}
		if(userRepository.existsByEmail(registerRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("User with this email already exists!"));
		}
		
		Employee newEmployee = new Employee(
				registerRequest.getUsername(),
				passwordEncoder.encode(registerRequest.getPassword()),
				registerRequest.getFirstName(),
				registerRequest.getLastName(),
				registerRequest.getSalary(),
				registerRequest.getEmail(),
				registerRequest.getDepartment(),
				registerRequest.getDesignation());
		
		Set<String> rolesString = registerRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		rolesString.forEach(role -> {
			switch (role) {
			case "admin":
			Role adminRole = roleRepository.findByName(RoleEnum.ADMIN).orElseThrow(()-> new RuntimeException("Error: Role" + role + "is not found")); 
				roles.add(adminRole);
				break;

			default:
				Role employeeRole = roleRepository.findByName(RoleEnum.EMPLOYEE).orElseThrow(() -> new RuntimeException("Error: Role" + role + "is not found")); 
				roles.add(employeeRole);
				break;
			}
		});
		newEmployee.setRoles(roles);
		userRepository.save(newEmployee);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
