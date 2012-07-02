package com.downforce.teamcowboy.rest.response;

public class Message {
	public int messageId;
	public String title;
	public String bodyHtml;
	public String bodyText;
	public boolean isPinned;
	public boolean allowComments;
	public int commentCount;
	public MessageTeamInfo team;
	public User postedBy;
	public MessageComment[] comments;
	public MessageUserMetaInfo userMetaInfo;
	public String dateCreatedLocal;
	public String dateLastUpdatedLocal;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;

	Message() {}
}
