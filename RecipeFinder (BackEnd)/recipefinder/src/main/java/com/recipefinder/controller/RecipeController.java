package com.recipefinder.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.recipefinder.service.RecipeService;
@RestController
public class RecipeController {
	@Autowired
	RecipeService service;
	RecipeService recipeService;
	
	/**
	 * @return
	 * Recieve File(image)
	 * Call service for result
	 * Send recipe reponse to client
	 * 
	 */

	@PostMapping("/upload")
	public ResponseEntity<String> getRecipe(@RequestParam("file") MultipartFile file) {

		
		recipeService.getRecipe(file);
		String message;
		try {
			
 
			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
		
	}
	
	@GetMapping(value="/check")
	public String checking() {
		return "hello";
	}
	
}
