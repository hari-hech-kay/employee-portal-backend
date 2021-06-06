 package com.example.demo.security;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JWTUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtils.class);
	
	@Value("${jwtSecret}")
	private String jwtSecret;
	private int jwtExpiration = 24;
	private Calendar calendar = Calendar.getInstance();
	
	public JWTUtils() {
		calendar.add(Calendar.HOUR, jwtExpiration);
	}

	public String generateJwtToken(Authentication authentication) {
		AppUserDetails userPrincipal = (AppUserDetails) authentication.getPrincipal();
		Claims claims = Jwts.claims();
		
		List<String> roles = userPrincipal.getAuthorities()
				.stream()
				.map(role -> role.getAuthority())
				.collect(Collectors.toList());
		
		claims.put("username", userPrincipal.getUsername());
		claims.put("id", userPrincipal.getId());
		claims.put("role", roles);
		
		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setClaims(claims)
				.setIssuedAt(new Date())
				.setExpiration(calendar.getTime())
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	public String getUsernameFromJwtToken(String jwtToken) {
		return Jwts.parser()
				.setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody().get("username", String.class);
	}
	
	public boolean validateJwtToken(String jwtToken) {
		try {
			Jwts.parser()
			.setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
			return true;
		}
		catch (SignatureException e) {
			LOGGER.error("Invalid signature: {}", e.getMessage());
		}
		catch (MalformedJwtException e) {
			LOGGER.error("Invalid token: {}", e.getMessage());
		}
		catch (ExpiredJwtException e) {
			LOGGER.error("Expired token: {}", e.getMessage());
		}
		catch (UnsupportedJwtException e) {
			LOGGER.error("Unsupported token: {}", e.getMessage());
		}
		catch (IllegalArgumentException e) {
			LOGGER.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false; 
	}
	
}
