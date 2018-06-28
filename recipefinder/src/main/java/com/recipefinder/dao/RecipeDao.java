package com.recipefinder.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.recipefinder.model.Recipe;

public class RecipeDao {
	
	@Autowired
	private RecipeRepository recipeRepository;
	
	public String getRecipeByName(String recipeName) {
		
		Recipe recipe=recipeRepository.findByName(recipeName);
		
		//convert to json and return
		return null;
	}
	
}
