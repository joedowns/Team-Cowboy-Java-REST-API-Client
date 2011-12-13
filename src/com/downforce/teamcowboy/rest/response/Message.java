package com.downforce.teamcowboy.rest.response;

public class Message {
	public int messageId;
	public String title;
	public String bodyHtml;
	public boolean isPinned;
	public boolean allowComments;
	public int commentCount;
	public Team team;
	public User postedBy;
	public MessageComment[] comments;
	public String dateCreatedLocal;
	public String dateLastUpdatedLocal;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;

	Message() {}
}
