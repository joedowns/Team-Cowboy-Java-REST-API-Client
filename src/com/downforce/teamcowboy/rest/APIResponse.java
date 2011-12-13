package com.downforce.teamcowboy.rest;

/**
 * Represents a response to an API call.
 * 
 * @author Joe Downs
 * @since 0.1
 */
public class APIResponse<T> {
	private boolean success;
	private T body;
	private Number requestSecs;
	private APIError error;
	
	public APIResponse(boolean success, Number requestSecs, T body, APIError error) {
		this.success = success;
		this.requestSecs = requestSecs;
		this.body = body;
		this.error = error;
	}
	
	/**
	 * Whether the call was successful or not.
	 */
	public boolean getSuccess() {
		return success;
	}
	
	/**
	 * The length of time, in seconds, the call took.
	 */
	public Number getRequestSecs() {
		return requestSecs;
	}
	
	/**
	 * An object representing the body of the response.
	 */
	public T getBody() {
		return body;
	}
	
	/**
	 * If an error occurred, this will contain information about it.
	 */
	public APIError getError() {
		return error;
	}
}
