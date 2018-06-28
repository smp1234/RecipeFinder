package com.recipefinder.service;

import java.io.File;

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
		
		return null;
	}
	
	public String getDBRecipe(String recipeName) {
		
		//calls DAO layer using recipeName 
		
		return null;
	}
}
