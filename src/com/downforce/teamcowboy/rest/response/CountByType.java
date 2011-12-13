package com.downforce.teamcowboy.rest.response;

public class CountByType {
	public String[] types;
	public Number[] counts;

	CountByType(String[] types, Number[] counts) {
		this.types = types;
		this.counts = counts;
	}
}