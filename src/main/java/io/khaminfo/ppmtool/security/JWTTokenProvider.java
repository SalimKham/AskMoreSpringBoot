package io.khaminfo.ppmtool.security;

import java.util.Date;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.khaminfo.ppmtool.domain.Student;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.domain.Student;
import io.khaminfo.ppmtool.exceptions.AccessException;
import io.khaminfo.ppmtool.repositories.UserRepository;

@Component
public class JWTTokenProvider {

	public String generateToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Date now = new Date(System.currentTimeMillis());
		Date expriryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);
		String userId = Long.toString(user.getId());
		Map<String, Object> claimsMap = new HashMap<>();
		claimsMap.put("id", userId);
		claimsMap.put("username", user.getUsername());
		claimsMap.put("type", user.getType());
		
		switch (user.getUser_state()) {
		case 5:
			throw new AccessException("Please Confirm your Account");

		case 4:
			throw new AccessException("Account Not Confirmed By Admin");

		case 3:
			throw new AccessException("Account Blocked By Admin ");

		default:
			break;
		}
		if(user.getType() == 2) {
			Student student = (Student)user;
			claimsMap.put("studentGroupes", student.getGroupesString());
		}

		return Jwts.builder().setSubject(userId).setClaims(claimsMap).setIssuedAt(now).setExpiration(expriryDate)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET).compact();
	}

	public boolean validateToken(String token) {
		try {

			Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			System.out.println("Invalid Sigature JWT");
		} catch (MalformedJwtException ex) {
			System.out.println("Invalid JWT Token");
		} catch (ExpiredJwtException ex) {
			System.out.println("Expired JWT Token");
		} catch (UnsupportedJwtException ex) {
			System.out.println("Unsupported JWT Token");
		} catch (IllegalArgumentException ex) {
			System.out.println("JWT claims string is empty");
		}
		return false;

	}

	public long getUserIdFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
		long id = Long.parseLong((String) claims.get("id"));
		return id;
	}

}
