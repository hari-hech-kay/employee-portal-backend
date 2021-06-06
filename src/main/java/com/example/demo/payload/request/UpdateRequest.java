package com.example.demo.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UpdateRequest {

	
	@NotBlank
	@Email
	private String email;
	
	@Override
	public String toString() {
		return "UpdateRequest [email=" + email + "firstName=" + firstName + ", lastName="
				+ lastName + ", salary=" + salary + ", department=" + department + ", designation=" + designation + "]";
	}
	//private String roles;
	
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	private float salary;
	private String department;
	private String designation;
	
//	public String getRole() {
//		return roles;
//	}
//	public void setRole(String role) {
//		this.roles = role;
//	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public float getSalary() {
		return salary;
	}
	public void setSalary(float salary) {
		this.salary = salary;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
}
