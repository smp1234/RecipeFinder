package com.recipefinder.dao;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.recipefinder.model.Properties;

@Repository
public class PropertiesDao {
	
	@Autowired
	private PropertiesRepository propertiesRepository;	
	
	public boolean addProperty(String name, int value) {
		boolean status = false;
		try {
			propertiesRepository.save(new Properties(name, value));
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		return status;		
	}
	
	public boolean updateProperties(Properties properties) {
		boolean status = false;
		try {
			propertiesRepository.save(properties);
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		return status;	
	}
	
	public Properties getPropertyByName(String name) {
		
		Properties property = propertiesRepository.findByName(name);
		if(property == null)
			return null;
		return property;
	}

}
