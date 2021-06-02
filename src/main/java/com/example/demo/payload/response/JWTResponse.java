package com.example.demo.payload.response;

import java.util.List;


public class JWTResponse {

	private String token;
	private String type = "Bearer";
	private String username;
	private String password;
	private List<String> roles;
	public JWTResponse(String token, String username, String password, List<String> roles) {
		super();
		this.token = token;
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	
}
