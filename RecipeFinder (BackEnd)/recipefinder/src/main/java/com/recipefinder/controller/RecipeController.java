package com.recipefinder.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.recipefinder.service.RecipeService;
@RestController
public class RecipeController {

	@Autowired
	RecipeService recipeService;
	
	/**
	 * @return
	 * Recieve File(image)
	 * Call service for result
	 * Send recipe reponse to client
	 * 
	 */

	@PostMapping("/upload")
	public String getRecipe(@RequestParam("file") MultipartFile file) {


		
		String response=recipeService.getRecipe(file);
		
		try {
			
			return response;
		} catch (Exception e) {
			response = "No recipe found!";

		
		
		
		
			return response;
		}
		
	}
	
	@GetMapping(value="/check")
	public String checking() {
		return recipeService.ServiceTest();
	}
	
	
}
