package com.recipefinder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Component
@Document(collection="Property")
public class Properties {
	
	@Id
	private String Id;
	private String name;
	private int value;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public Properties() {}
	public Properties(String name, int value) {
		
		this.name = name;
		this.value = value;
	}
	
	
}
