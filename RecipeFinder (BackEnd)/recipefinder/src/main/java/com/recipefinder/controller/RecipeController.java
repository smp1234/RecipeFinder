package com.recipefinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.recipefinder.model.Recipe;
import com.recipefinder.service.RecipeService;
@RestController
public class RecipeController {
	@Autowired
	RecipeService service;
	
	/**
	 * @return
	 * Recieve File(image)
	 * Call service for result
	 * Send recipe reponse to client
	 * 
	 */
	@RequestMapping(value="test", method = RequestMethod.GET)
	public String test() {
		return service.ServiceTest();
	}
	public String getRecipe() {
		
		
		return null;
		
	}
	
}
