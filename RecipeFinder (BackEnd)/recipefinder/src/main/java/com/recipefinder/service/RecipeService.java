package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefinder.dao.RecipeDao;
@Service
public class RecipeService {
	RecipeDao recipeDao = new RecipeDao();

	public String getRecipe(MultipartFile multipartImage) {
		// recieve image
		File image = new File("./images/" + multipartImage.getOriginalFilename());

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
			String response = getDBRecipe(recipeName);
			return response;
		}

	}

	public String predictRecipe(String fileName) {

		// call prediction model to get recipe name
		Process p;
		String result = null;
		try {
			String pythonPath = "./ML/ml.py";
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList("python", pythonPath, fileName));
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
		String image = "./images/chickenb1.jpg";
		String recipeName = predictRecipe(image);
		String response = getDBRecipe(recipeName);
		return response;
	}

}
