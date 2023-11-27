package com.github.bbugsco;

public class Main {
    public static void main( String[] args ) {
		String token = System.getenv("API_KEY");
		Bot bot =  new Bot(token);
    }
}
