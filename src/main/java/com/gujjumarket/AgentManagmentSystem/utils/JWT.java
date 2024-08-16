package com.gujjumarket.AgentManagmentSystem.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;

public class JWT {

	private static SecretKey jwtsecret;
	private static long jwttimeout;
	private static Set<String> tokenBlacklist = new HashSet<>();
	
	public JWT(String jwtsecret, Long jwttimeout) {

		JWT.setJwtsecret(Keys.hmacShaKeyFor(jwtsecret.getBytes()));
		JWT.jwttimeout = jwttimeout;
	}

	public static String generateToken(int i,String role) throws InvalidKeyException {
		return Jwts.builder().subject(role).claim("userId", i).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwttimeout))
				.signWith(getJwtsecret(), Jwts.SIG.HS384).compact();
	}

	public static boolean validateToken(String token) {
		if (JWT.isTokenBlacklisted(token)) {
            return false; // The token is blacklisted
        }
		try {

			Jws<Claims> claims = Jwts.parser().verifyWith(getJwtsecret()).build().parseSignedClaims(token);
			if (claims.getPayload().getExpiration().before(new Date())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			return false;
		}
	}

	public static SecretKey getJwtsecret() {
		return jwtsecret;
	}

	public static void setJwtsecret(SecretKey jwtsecret) {
		JWT.jwtsecret = jwtsecret;
	}
	
	 public static void blacklistToken(String token) {
		 System.out.println("token blacklisted"+ token);
	        tokenBlacklist.add(token);
	    }

	    public static boolean isTokenBlacklisted(String token) {
	        return tokenBlacklist.contains(token);
	    }
	
	    

}
