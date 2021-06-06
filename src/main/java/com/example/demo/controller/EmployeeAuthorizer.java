package com.example.demo.controller;

import org.springframework.stereotype.Service;

import com.example.demo.security.AppUserDetails;

@Service
public class EmployeeAuthorizer {

	public boolean isEmployeeCurrentlyLoggedIn(AppUserDetails principal, long id){
		System.out.println(principal.getId());
		return id == principal.getId();
	}
}
