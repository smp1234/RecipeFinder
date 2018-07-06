package com.recipefinder.model;

import java.util.HashSet;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="Recipe")
public class Recipe {
	@Id
	private String Id;
	private String name;
	private HashSet<String> ingredients;
	private List<String> steps;
	private String origin;
	private String preparation;
	public String getName() {
		return name;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashSet<String> getIngredients() {
		return ingredients;
	}
	public void setIngredients(HashSet<String> ingredients) {
		this.ingredients = ingredients;
	}
	public List<String> getSteps() {
		return steps;
	}
	public void setSteps(List<String> steps) {
		this.steps = steps;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Recipe() {
		super();
	}
	public Recipe(String name, HashSet<String> ingredients, List<String> steps, String origin) {
		super();
		this.name = name;
		this.ingredients = ingredients;
		this.steps = steps;
		this.origin = origin;
	}
	public String getPreparation() {
		return preparation;
	}
	public void setPreparation(String preparation) {
		this.preparation = preparation;
	}
	

}
