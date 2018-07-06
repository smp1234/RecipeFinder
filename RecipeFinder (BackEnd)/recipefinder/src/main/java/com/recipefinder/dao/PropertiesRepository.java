package com.recipefinder.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.recipefinder.model.Properties;

public interface PropertiesRepository extends MongoRepository<Properties, String> {
	public Properties findByName(String name);
}
