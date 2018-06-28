package com.recipefinder.model;

import java.util.HashMap;

public class Recipe {
	
	private String name;
	private HashMap<String,Integer> ingredients;
	private String steps;
	private String origin;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, Integer> getIngredients() {
		return ingredients;
	}
	public void setIngredients(HashMap<String, Integer> ingredients) {
		this.ingredients = ingredients;
	}
	public String getSteps() {
		return steps;
	}
	public void setSteps(String steps) {
		this.steps = steps;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	

}
