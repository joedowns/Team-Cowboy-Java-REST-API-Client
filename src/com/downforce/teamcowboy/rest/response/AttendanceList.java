package com.downforce.teamcowboy.rest.response;

public class AttendanceList {
	public StatusCount[] countsByStatus;
	public AttendanceListMetadata meta;
	public StatusUser[] usersIdsByStatus;
	public AttendanceListUser[] users;

	AttendanceList() {}
}
