package com.example.crawler;
import java.util.Scanner;

public class alpahconvert {
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		Boolean b = true;
		while(b) {
			
			String input = scanner.nextLine();
			char[] inputs = input.toCharArray();
			String output ="";
			for(char c : inputs) {
				if(64<c && c<95) {
					c+=32;
					output+=String.valueOf(c);
				}
				else if(c>=97 && c<=122){
					c-=32;
					output+=String.valueOf(c);
				}
				else
					output+=c;
					
				
			}
			System.out.println();
			System.out.print(output);
		}
	}
}

