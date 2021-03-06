package com.recipefinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@GetMapping("/login")
	public int login(@RequestParam("emailId") String emailId) {
		return recipeService.getUserId(emailId);
	}
	
	@PostMapping("/signup")
	public int signup(@RequestParam("emailId") String emailId) {
		return recipeService.getUserId(emailId);
	}

	@PostMapping("/upload")
	public String getRecipe(@RequestParam("file") MultipartFile file, @RequestParam("uid") int uid) {
		String response=recipeService.getRecipe(file,uid);
		
		try {
			
			return response;
		} catch (Exception e) {
			response = "No recipe found!";

			return response;
		}
		
	}
	
	@GetMapping("/getundetecteditems")
	public String getUndetectedItems(@RequestParam("userId") int userId) {
		return recipeService.getUndetectedItemsForUser(userId).toString();
	}
	
	@PostMapping("/vote")
	public String addVote(@RequestParam("filename") String fileName, @RequestParam("vote") String vote, @RequestParam("userId") int userId) {
		boolean status = recipeService.handleVote(fileName, vote, userId);
		if(status)
			return "Vote added successfully";
		else
			return "Error in adding vote!";
	}
	
	@GetMapping("/notifications")
	public String sendNotification(@RequestParam("userId") int userId) {
		return recipeService.sendNotification(userId);
	}
	
	@GetMapping(value="/check")
	public String checking() {
		System.out.print("Inside Check...");
		return recipeService.Test();
	}
	
	
}
