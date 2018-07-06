package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefinder.dao.RecipeDao;
import com.recipefinder.model.Recipe;

@Service
public class RecipeService {
	@Autowired
	RecipeDao recipeDao;
	@Autowired
	Recipe recipe;

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

	public String searchWebRecipe(String recipeName) {
		String[] recipeWords = recipeName.split(" ");
		String searchURL_2 = "https://www.allrecipes.com/search/results/?wt=";
		String searchURL_1 = "https://www.foodfusion.com/recipe?s=";
		String negResp = "could not find recipe for " + recipeName;
		int len = recipeWords.length;
		//need to remove after testing
		//Recipe recipe= new Recipe();
		recipe.setName(recipeName);

		HashSet<String> ingredients = new HashSet<>();
		List<String> directions = new ArrayList<>();
		String searchURL = searchURL_1;
		for (int i = 0; i < len - 1; i++) {
			searchURL = searchURL + recipeWords[i] + "+";
		}
		searchURL = searchURL + recipeWords[len - 1];
		try {
			// Connectiong URL_1
			Document doc = Jsoup.connect(searchURL).get();

			String linkHref = "";

			// Retrieving first link from the results
			Element recipeDiv = doc.getElementById("load_more_recipes_archives");
			if (recipeDiv != null && recipeDiv.getElementsByAttributeValue("class", "grid-img-link") != null && recipeDiv.getElementsByAttributeValue("class", "grid-img-link").first()!=null)
				linkHref = recipeDiv.getElementsByAttributeValue("class", "grid-img-link").first().attr("href");

			System.out.println("URL::" + linkHref);

			// checking if link is the correct recipe
			boolean isCorrect = true;
			for (int i = 0; i < len; i++) {
				if (!linkHref.contains(recipeWords[i])) {
					isCorrect = false;
				}
			}
			if (isCorrect) {
				// connecting to the correct recipe link
				doc = Jsoup.connect(linkHref).get();
				System.out.println(linkHref);

				// retrieving the div containing ingredients and directions
				recipeDiv = doc.getElementsByAttributeValue("class", "english-detail-ff").first();

				for (Element ingreList : recipeDiv.getElementsByTag("li")) {
					ingredients.add(ingreList.text());

				}
				recipe.setIngredients(ingredients);
				for (Element direcList : recipeDiv.getElementsByTag("p")) {
					if (!direcList.text().isEmpty()) {
						directions.add(direcList.text());

					}
				}
				recipe.setSteps(directions);
				recipe.setPreparation("");
				ObjectMapper mapper = new ObjectMapper();

				// Object to JSON in String
				String jsonInString = mapper.writeValueAsString(recipe);
				return jsonInString;

			} else {

				// connecting to URL_2 for the recipe
				searchURL = searchURL_2;
				for (int i = 0; i < len; i++) {
					searchURL = searchURL + recipeWords[i] + "+";
				}
				searchURL = searchURL + recipeWords[len - 1];
				doc = Jsoup.connect(searchURL).get();
				// retrieving the recipe link
				Element result = doc.getElementsByAttributeValue("data-internal-referrer-link", "hub recipe").first();
				if (result != null)
					linkHref = result.attr("href");
				// checking if correct recipe
				isCorrect = true;
				for (int i = 0; i < len; i++) {
					if (!linkHref.contains(recipeWords[i])) {
						isCorrect = false;
					}
				}
				if (isCorrect) {
					// connecting to the recipe link
					doc = Jsoup.connect(searchURL).get();
					String temp = "";

					// retrieving the recipe ingredients
					for (Element result1 : doc.getElementsByAttributeValue("class", "checkList__line")) {

						Element result2 = result1.getElementsByTag("label").first();

						// String linkText = result.text();

						if (result2 != null) {
							temp = result2.attr("title");
							if (!temp.equals(""))
								ingredients.add(result2.attr("title"));
						}

					}
					recipe.setIngredients(ingredients);
					temp = "";
					// retrieving the recipe preparation time if present
					for (Element result1 : doc.getElementsByAttributeValue("class", "prepTime__item")) {
						System.out.println("preparation time" + result1.attr("aria-label"));
						temp = temp + result1.attr("aria-label") + " ";
					}
					recipe.setPreparation(temp);

					temp = "";
					// retrieving the recipe directions

					Element result1 = doc.getElementsByAttributeValue("itemprop", "recipeInstructions").first();
					for (Element steps : result1.getElementsByTag("span")) {

						if (steps.text() != null && !steps.text().isEmpty()) {
							directions.add(steps.text());
						}

					}
					recipe.setSteps(directions);
					// Converting to JSON object
					ObjectMapper mapper = new ObjectMapper();

					// Object to JSON in String
					String jsonInString = mapper.writeValueAsString(recipe);
					return jsonInString;

				} else {

					// if recipe not present in URL_1 and URL_2 sending no recipe found

					System.out.println(negResp);
					return negResp;
				}
			}

			// System.out.println(doc.html());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return negResp;
		}

	}
	
	public String ServiceTest() {
		String image = "images/chickenb1.jpg";
		String recipeName = predictRecipe(image);
		String response = getDBRecipe(recipeName);
		return response;
	}

}
