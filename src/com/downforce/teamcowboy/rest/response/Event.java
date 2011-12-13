package com.downforce.teamcowboy.rest.response;

//import java.util.Map;

public class Event {
	public int eventId;
	public EventTeam team;
	public int seasonId;
	public String seasonName;
	public String eventType;
	public String eventTypeDisplay;
	public String status;
	public String statusDisplay;
	public String personNounSingular;
	public String personNounPlural;
	public String title;
	public String titleLabel;
	public String homeAway;
	public EventResult result;
	public RSVPInstance[] rsvpInstances;
	public String comments;
	//public Map<String, String> options;
	public String oneLineDisplay;
	public String oneLineDisplayShort;
	public String maleGenderDisplay;
	public String femaleGenderDisplay;
	public DateTimeInfo dateTimeInfo;
	public Location location;
	public EventShirtColors shirtColors;
	public String dateLastUpdatedUtc;
	
	Event() {}
}
