package com.downforce.teamcowboy.rest;

/**
 * Represents an error result from an API call.
 * 
 * @author Joe Downs
 * @since 0.1
 */
public class APIError {
	private String errorCode;
	private int httpResponse;
	private String message;
	
	public APIError(String errorCode, int httpResponse, String message) {
		this.errorCode = errorCode;
		this.httpResponse = httpResponse;
		this.message = message;
	}
	
	/**
	 * The unique error code for the error.
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * The HTTP/1.1 status code that was returned.
	 */
	public int getHttpResponse() {
		return httpResponse;
	}
	
	/**
	 * A message describing the error in more detail.
	 */
	public String getMessage() {
		return message;
	}
}
