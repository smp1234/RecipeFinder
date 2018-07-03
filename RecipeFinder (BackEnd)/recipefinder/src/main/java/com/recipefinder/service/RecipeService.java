package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefinder.dao.RecipeDao;
@Service
public class RecipeService {
	@Autowired
	RecipeDao recipeDao;
	
	public String getRecipe(MultipartFile multipartImage) {
		// recieve image
		File image = new File("./images/"+multipartImage.getOriginalFilename());

		boolean op = false;
		try {
			image.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(image);
			outputStream.write(multipartImage.getBytes());
			outputStream.close();
			op = true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (op == false)
			return "Error in creating file...";
		else {
			String recipeName = predictRecipe(image.getPath());
			String response;
			if(recipeName == null || recipeName.isEmpty() || recipeName.equals("Unable to detect item. Please try again...")) {
				response = "Unable to detect item. Please try again!";
				return response;
			}
			response = getDBRecipe(recipeName);
			
			return response;
		}

		
	}

	public String predictRecipe(String fileName) {

		// call prediction model to get recipe name
		String result = "";		
		try {
			String image = fileName;
			String pythonPath = "ML/ml.py";			
			
			String[] command = new String[] {"C:\\Users\\spatel\\AppData\\Local\\Programs\\Python\\Python36\\python.exe", pythonPath, image};
			try {
				Process process = Runtime.getRuntime().exec(command);
				BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));
				result = stream.readLine();
				System.out.println(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e);
		} 
		return result;
	}

	public String getDBRecipe(String recipeName) {

		// calls DAO layer using recipeName
		String data = null;
		try {
			data = recipeDao.getRecipeByName(recipeName);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public String ServiceTest() {
		String image = "images/chickenb1.jpg";
		String recipeName = predictRecipe(image);
		String response = getDBRecipe(recipeName);
		return response;
	}

}
