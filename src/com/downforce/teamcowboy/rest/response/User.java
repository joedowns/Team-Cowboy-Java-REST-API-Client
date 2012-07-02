package com.downforce.teamcowboy.rest.response;

//import java.util.Map;

public class User {
	public Integer userId;
	public String firstName;
	public String lastName;
	public String fullName;
	public String displayName;
	public String emailAddress1;
	public String emailAddress2;
	public String phone1;
	public String phone2;
	public String gender;
	public String genderDisplay;
	public String shirtNumber;
	public String shirtSize;
	public String pantsSize;
	//public Map<String, String> options
	public ProfilePhoto profilePhoto;
	public TeamMetadata teamMeta;
	public LinkedUsers linkedUsers;
	public String dateCreatedUtc;
	public String dateLastUpdatedUtc;
	public String dateLastSignInUtc;
	public Integer birthDate_month;
	public Integer birthDate_day;
	public Integer birthDate_year;

	User() {}
}
