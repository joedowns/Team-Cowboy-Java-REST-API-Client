package com.downforce.teamcowboy.rest.response;

public class Team {
	public int teamId;
	public String name;
	public String shortName;
	public TeamType type;
	public Activity activity;
	public String timezoneId;
	public String city;
	public String stateProvince;
	public String stateProvinceAbbrev;
	public String country;
	public String countryIso3;
	public String postalCode;
	public String locationDisplayShort;
	public String locationDisplayLong;
	public TeamSimpleUser managerUser;
	public TeamSimpleUser captainUser;
	public ProfilePhoto teamPhoto;
	public TeamColorSwatches colorSwatches;
	public TeamOptions options;
	public TeamUser userProfileInfo;
	public TeamMetadata meta;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;

	Team() {}
}
