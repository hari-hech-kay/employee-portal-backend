package com.example.demo.security;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JWTUtils jwtUtils;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwtToken = parseJwt(request);
			if(jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
				String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication
				= new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication );
				
			}
		}
		catch(Exception e) {
			LOGGER.error("Cannot set user authentication: {}", e);
	}
		filterChain.doFilter(request, response);

}
	
	private String parseJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer")){
			return authHeader.substring(7, authHeader.length());
		}
		return null;
		
	}
}
	
