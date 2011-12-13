package com.downforce.teamcowboy.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Real implementation for making HTTP calls to the Team Cowboy REST API. 
 * 
 * @author Joe Downs
 * @since 0.1
 */
public class HttpProviderImpl implements IHttpProvider {
	public String makeHTTPCall(String url, String body, String httpRequestMethod) throws MalformedURLException, IOException {
		URL realUrl = new URL(url);
		URLConnection conn = realUrl.openConnection();
		conn.setRequestProperty("method", httpRequestMethod);
		conn.setDoInput(true);
		if (body != null && body != "") {
			conn.setDoOutput(true);
			conn.getOutputStream().write(body.getBytes());
		} else {
			conn.connect();
		}
		
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[1024];
		int len;
		InputStreamReader reader = new InputStreamReader(conn.getInputStream());
		while ((len = reader.read(buffer, 0, buffer.length)) > 0) {
			builder.append(buffer, 0, len);
		}
		return builder.toString();
	}
}
