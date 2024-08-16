package com.gujjumarket.AgentManagmentSystem.utils;

import org.mindrot.jbcrypt.BCrypt;

public class passwordHasher {
	
	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
	
	public static boolean verifyPassword(String password,String hashPassword) {
		return BCrypt.checkpw(password, hashPassword);
	}

	
}
