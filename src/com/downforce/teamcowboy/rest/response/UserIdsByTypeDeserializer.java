package com.downforce.teamcowboy.rest.response;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class UserIdsByTypeDeserializer implements JsonDeserializer<UserIdsByType> {
	public UserIdsByType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Set<Entry<String, JsonElement>> props = json.getAsJsonObject().entrySet();
		String[] types = new String[props.size()];
		Number[][] userIds = new Number[props.size()][];
		int i=0;
		for (Entry<String, JsonElement> entry : props) {
			types[i] = entry.getKey();
			JsonArray jsonArray = entry.getValue().getAsJsonArray();
			Number[] userIdsForType = new Number[jsonArray.size()];
			for (int j=0;j<userIdsForType.length;++j) {
				userIdsForType[j] = jsonArray.get(j).getAsNumber();
			}
			userIds[i++] = userIdsForType;
		}
		
		return new UserIdsByType(types, userIds);
	}
}