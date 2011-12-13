package com.downforce.teamcowboy.rest;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * An interface for abstracting out the HTTP call to the API. Used to inject mock providers for testing.
 * 
 * @author Joe Downs
 * @since 0.1
 */
public interface IHttpProvider {
	/**
	 * Make a call to a URL over HTTP and return the result.
	 * 
	 * @param url the URL to use
	 * @param body the body of the request, if any
	 * @param httpRequestMethod the HTTP request method to use (i.e. "POST", "GET")
	 * @return the response from the HTTP call
	 */
	String makeHTTPCall(String url, String body, String httpRequestMethod) throws MalformedURLException, IOException;
}
