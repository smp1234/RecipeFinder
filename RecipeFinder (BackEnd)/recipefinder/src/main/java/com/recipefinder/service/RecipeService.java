package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefinder.dao.RecipeDao;

public class RecipeService {
	RecipeDao recipeDao = new RecipeDao();

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
		String result = null;
		try {
			String imagePath = "./images/"+fileName;
			String pythonPath = "./ML/ml.py";
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList("python", pythonPath,imagePath));
			p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			result = in.readLine();
			p.destroy();
		} catch (Exception e) {
			System.out.println(e);
		} 
		return result;
	}
	
	public String getDBRecipe(String recipeName) {
		
		//calls DAO layer using recipeName 
		String data = null;
		try {
			data = recipeDao.getRecipeByName(recipeName);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
