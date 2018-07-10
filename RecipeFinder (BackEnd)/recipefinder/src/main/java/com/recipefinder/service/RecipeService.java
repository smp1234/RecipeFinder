package com.recipefinder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefinder.dao.NotificationDao;
import com.recipefinder.dao.PropertiesDao;
import com.recipefinder.dao.RecipeDao;
import com.recipefinder.dao.UndetectedItemDao;
import com.recipefinder.dao.UserDao;
import com.recipefinder.dao.VoteDao;
import com.recipefinder.model.Notification;
import com.recipefinder.model.Properties;
import com.recipefinder.model.Recipe;
import com.recipefinder.model.UndetectedItem;
import com.recipefinder.model.UnknownEntry;
import com.recipefinder.model.User;

@Service
public class RecipeService {
	@Autowired
	RecipeDao recipeDao;
	@Autowired
	Recipe recipe;	
	@Autowired
	private UserDao userDao;
	@Autowired
	private UndetectedItemDao undetectedItemDao;
	@Autowired
	private UndetectedItem item;
	@Autowired
	private VoteDao voteDao;
	@Autowired
	private NotificationDao notificationDao;
	@Autowired
	private Notification notification;
	@Autowired
	private User user;
	@Autowired
	private PropertiesDao propertiesDao;
	@Autowired
	private Properties property;
		
	public int getUserId(String emailId) {
		user = userDao.getUserByEmailId(emailId);
		if(user == null) {
			int id;	
			property = propertiesDao.getPropertyByName("userId");
			if(property == null) {
				id = 1;				
				propertiesDao.addProperty("userId", id + 1);
			}
			else {
				id = property.getValue();		
				property.setValue(id + 1);
				propertiesDao.updateProperties(property);
			}
			
			userDao.addUser(emailId, id);
			int globalNotificationCount;
			property = propertiesDao.getPropertyByName("globalNotificationCount");
			if(property == null) {
				globalNotificationCount = 0;				
				propertiesDao.addProperty("globalNotificationCount", 0);
			}
			else {
				globalNotificationCount = property.getValue();
			}
			notificationDao.createEntry(userDao.getUserByUserId(id), globalNotificationCount);
			return id;
		}
		return user.getUserId();	
	}

	public String getRecipe(MultipartFile multipartImage, int userId) {
		// Receive image
		 File image = new File("./images/"+multipartImage.getOriginalFilename());

//		File image = null;
		boolean op = false;
		try {
			image.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(image);
			outputStream.write(multipartImage.getBytes());
			
			outputStream.close();
//			Files.copy(Paths.get(multipartImage.getPath()), Paths.get("./images/"+multipartImage.getName()), StandardCopyOption.REPLACE_EXISTING);
//			image = new File("./images/" + multipartImage.getName());
			op = true;

		} catch (Exception e) {			
			e.printStackTrace();
		}
		if (op == false)
			return "Error in creating file...";
		else {
			String recipeName = predictRecipe(image.getPath());
//			String recipeName = "Unable to detect item. Please try again...";
			String response;
			if(recipeName == null || recipeName.isEmpty() || recipeName.equals("Unable to detect item. Please try again...")) {
				response = "Unable to detect item. Please try again!";
				if(recipeName == null || recipeName.equals("Unable to detect item. Please try again...")) {
					boolean status = handleUndetectedImage(image,userId);
					if(status == true)
						return "Unable to detect item. We are asking other user to comment over it.";
					else
						return "Unable to detect item. Error while asking the other user.";
				}
								
				return response;
			}
			response = getDBRecipe(recipeName);
			if(response == null)
				response = searchWebRecipe(recipeName);
			try {
				Files.delete(Paths.get(image.getPath()));
			} catch (IOException e) {				
				e.printStackTrace();
			}
			return response;
		}		
	}

	public String predictRecipe(String fileName) {

		// call prediction model to get recipe name
		String result = "";		
		try {
			String image = fileName;
			String pythonPath = "ML/predict.py";			
			
			String[] command = new String[] {"C:\\Users\\spatel\\AppData\\Local\\Programs\\Python\\Python36\\python.exe", pythonPath, image};
			try {
				Process process = Runtime.getRuntime().exec(command);
				BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));
				result = stream.readLine();
				System.out.println(result);
			} catch (IOException e) {		
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
		} catch (Exception e) {
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
				if (!linkHref.contains(recipeWords[i].toLowerCase())) {
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
					if (!linkHref.contains(recipeWords[i].toLowerCase())) {
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
	
	public boolean handleUndetectedImage(File image, int userId) {
		boolean status = false;		
		String newPath = "./undetected/" + userId +"_" + image.getName();
		try {
			Files.move(Paths.get(image.getPath()), Paths.get(newPath),StandardCopyOption.REPLACE_EXISTING);
			status = undetectedItemDao.addItem(image.getName(), userDao.getUserByUserId(userId));
			property = propertiesDao.getPropertyByName("globalNotificationCount");
			int globalNotificationCount = property.getValue();
			globalNotificationCount += 1;			
			property.setValue(globalNotificationCount);
			propertiesDao.updateProperties(property);			
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}
		return status;
	}
	
	public boolean handleVote(String fileName, String vote, int userId) {
		vote = vote.toLowerCase();
		boolean status = false;
		try {
			item = undetectedItemDao.getItemByName(fileName);
			Iterator<UnknownEntry> iterator = item.getGuessedItems().iterator();
			while (iterator.hasNext()) {
				UnknownEntry temp = iterator.next();
				if(temp.getName().equals(vote)) {
					temp.setVotes(temp.getVotes() + 1);
					if(temp.getVotes() >= 3) {
						handleDetection(item.getCreator().getUserId() + "_" + fileName, vote, fileName);
					}
					else {
						undetectedItemDao.updateItem(item);
					}
					if(voteDao.checkUserExistance(userId) == false) {
						status = voteDao.addVote(userDao.getUserByUserId(userId), undetectedItemDao.getItemByName(fileName));
					}
					else {
						status = voteDao.updateVote(userDao.getUserByUserId(userId), undetectedItemDao.getItemByName(fileName));
					}
					return true;
				}
			}
			item.getGuessedItems().offer(new UnknownEntry(vote, 1));
			undetectedItemDao.updateItem(item);
			if(voteDao.checkUserExistance(userId) == false) {
				status = voteDao.addVote(userDao.getUserByUserId(userId), undetectedItemDao.getItemByName(fileName));
			}
			else {
				status = voteDao.updateVote(userDao.getUserByUserId(userId), undetectedItemDao.getItemByName(fileName));
			}			
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		return status;
	}
	
	public boolean handleDetection(String filePath, String result, String fileName) {
		
		boolean status = false;
		try {
			// Create a directory with name of result
			File dir = new File("./dataset/" + result);
			dir.mkdir();
			// Move file to new directory (If there exists a file with same name it will be replaced.)
			Files.move(Paths.get("./undetected/" + filePath), Paths.get("./dataset/" + result +"/" + filePath), StandardCopyOption.REPLACE_EXISTING);
			// Delete entry from database
			status = undetectedItemDao.deleteItem(fileName);
			
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		return status;
	}
	
	public boolean checkForExpiry(String fileName) {
		boolean status = false;
		try {
			
			item = undetectedItemDao.getItemByName(fileName);
			long diff = Math.abs(new Date().getTime() - item.getEntryDate().getTime());
			long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if(days >= 3)
				status = undetectedItemDao.deleteItem(fileName);
			else
				status = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}			
		return status;
	}
	
	public HashMap<String, String> getAllUndetectedItems(){
		HashMap<String,String> result = new HashMap<>();
		HashMap<String, Integer> list = undetectedItemDao.getAllUndetectedItems();
		for(Entry<String, Integer> entry: list.entrySet()) {
			String path = "./undetected/" + entry.getValue() +"_" + entry.getKey();
			result.put(entry.getKey(),path);
		}
		return result;
	}
	
	public HashMap<String, String> getUndetectedItemsForUser(int uid){		
		HashMap<String, String> result = getAllUndetectedItems();
		HashSet<UndetectedItem> votedItems = voteDao.getVotedItems(userDao.getUserByUserId(uid));
		if(votedItems != null) {
			for(UndetectedItem undetectedItem: votedItems)
				result.remove(undetectedItem.getFileName());
		}
		List<UndetectedItem> itemsCreatedByUser = undetectedItemDao.getItemsByCreator(userDao.getUserByUserId(uid));
		if(itemsCreatedByUser != null) {
			for(UndetectedItem undetectedItem: itemsCreatedByUser)
				result.remove(undetectedItem.getFileName());
		}
		return result;
	}
	
	public String sendNotification(int userId) {
		notification = notificationDao.getNotificationEntry(userDao.getUserByUserId(userId));
		property = propertiesDao.getPropertyByName("globalNotificationCount");
		int globalNotificationCount = property.getValue();
		long result = (globalNotificationCount - notification.getNoOfNotifications());
		if(result > 0) {
			notification.setNoOfNotifications(globalNotificationCount);
			notificationDao.updateNotificationCount(notification);
		}
		return Long.toString(result);
	}
	
	public String Test() {
//		int userId = getUserId("spatel");
//		File file = new File("./test/fal.jpg");
//		getRecipe(file, userId);
//		getAllUndetectedItems();
//		handleVote(file.getName(), "falooda", 1);
		return "HI";
	}


}
