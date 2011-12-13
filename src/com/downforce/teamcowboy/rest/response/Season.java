package com.downforce.teamcowboy.rest.response;

public class Season {
	public int seasonId;
	public int teamId;
	public String name;
	public String startDateLocal;
	public String startDateUtc;
	public boolean startDateInFuture;
	public Activity activity;
	public League league;
	public String leagueDivision;

	Season() {}
}
