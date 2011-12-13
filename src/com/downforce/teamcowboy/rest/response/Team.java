package com.downforce.teamcowboy.rest.response;

public class Team {
	public int teamId;
	public String name;
	public String shortName;
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
	public ProfilePhoto teamPhoto;
	public TeamOptions options;
	public User userProfileInfo;
	public TeamMetadata meta;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;

	Team() {}
}
