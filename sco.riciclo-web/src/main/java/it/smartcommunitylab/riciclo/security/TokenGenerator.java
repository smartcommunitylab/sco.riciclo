package it.smartcommunitylab.riciclo.security;

import org.apache.commons.lang.RandomStringUtils;

public class TokenGenerator {

	public static String getToken(int length) {
		String token = RandomStringUtils.random(length, 0, 0, true, true);
		return token;
	}
	
	public static void main(String[] args) {
		System.out.println(TokenGenerator.getToken(16));
	}
}
