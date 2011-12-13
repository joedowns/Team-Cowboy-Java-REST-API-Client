package com.downforce.teamcowboy.rest.response;

public class UserIdsByType {
	public String[] types;
	public Number[][] userIds;

	UserIdsByType(String[] types, Number[][] userIds) {
		this.types = types;
		this.userIds = userIds;
	}
}
