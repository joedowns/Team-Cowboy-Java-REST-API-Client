package com.downforce.teamcowboy.rest.response;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeInfo {
	public String timezoneId;
	public String startDateLocal;
	public String startTimeLocal;
    public String startDateTimeLocal;
    public String startDateLocalDisplay;
	public String startTimeLocalDisplay;
	public String startDateTimeLocalDisplay;
	public String startDateTimeUtc;
	public Boolean startTimeTBD;
    public String endDateLocal;
    public String endTimeLocal;
    public String endDateTimeLocal;
    public String endDateLocalDisplay;
    public String endTimeLocalDisplay;
    public String endDateTimeLocalDisplay;
    public String endDateTimeUtc;
	public Boolean endTimeTBD;
	public Boolean inPast;
	public Boolean inFuture;

	DateTimeInfo() {}
	
	public Date getStartDateTimeLocal() {
		return getDate(startDateLocal, startTimeLocal);
	}
	
	public Date getEndDateTimeLocal() {
		return getDate(endDateLocal, endTimeLocal);
	}
	
	private Date getDate(String date, String time) {
		if (date == null || time == null) return null;
		
		String[] dateSplit = date.split("-");
		String[] timeSplit = time.split(":");
		
		if (dateSplit.length != 3 || timeSplit.length != 3) return null;
		
		return new GregorianCalendar(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]), 
				        Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]), Integer.parseInt(timeSplit[2])).getTime();
	}
}
