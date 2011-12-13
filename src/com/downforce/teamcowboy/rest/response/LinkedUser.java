package com.downforce.teamcowboy.rest.response;

public class LinkedUser {
	public Integer fromUserId;
	public Integer toUserId;
	public String username;
	public String firstName;
	public String lastName;
	public String fullName;
	public String displayName;
	public Boolean isActive;
	public ProfilePhoto profilePhoto;
	public LinkedUserTeam[] teams;

	LinkedUser() {}
}
