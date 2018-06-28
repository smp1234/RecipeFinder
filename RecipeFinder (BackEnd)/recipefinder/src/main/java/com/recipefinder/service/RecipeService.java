package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

public class RecipeService {

	public String getRecipe(File image) {
		//recieve image
		
		//save on server
		
		//call prediction function by sending image name 
		
		//on result of prediction query the DB(dao layer)
		
		//return result on converting to json
		
		return null;
	}
	
	
	public String predictRecipe(String fileName) {
		
		//call prediction model to get recipe name
		Process p;
		try {
			String path = "C:\\Users\\spatel\\Desktop\\project.py";
			String imagePath = "C:/Users/spatel/Desktop/chickenb1.jpg";
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList("python", "project.py","./images/chickenb1.jpg"));
			p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			System.out.println("Answer: " + in.readLine());
			p.destroy();
		} catch (Exception e) {
			System.out.println(e);
		} 
		return null;
	}
	
	public String getDBRecipe(String recipeName) {
		
		//calls DAO layer using recipeName 
		
		return null;
	}
}
