package com.downforce.teamcowboy.rest.response;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CountByTypeDeserializer implements JsonDeserializer<CountByType> {
	public CountByType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Set<Entry<String, JsonElement>> props = json.getAsJsonObject().entrySet();
		String[] types = new String[props.size()];
		Number[] counts = new Number[props.size()];
		int i=0;
		for (Entry<String, JsonElement> entry : props) {
			types[i] = entry.getKey();
			counts[i++] = entry.getValue().getAsNumber();
		}
		
		return new CountByType(types, counts);
	}
}
