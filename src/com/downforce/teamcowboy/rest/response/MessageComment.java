package com.downforce.teamcowboy.rest.response;

public class MessageComment {
	public int commentId;
	public int messageId;
	public int teamId;
	public String timezoneId;
	public User postedBy;
	public String dateCreatedLocal;
	public String dateLastUpdatedLocal;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;
	
	MessageComment() {}
}
