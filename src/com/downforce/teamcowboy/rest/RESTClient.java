package com.downforce.teamcowboy.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.downforce.teamcowboy.rest.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A REST Client to communicate with Team Cowboy. Please see http://api.teamcowboy.com/v1/docs/ 
 * for current documentation.
 * 
 * @author Joe Downs
 * @since 0.1
 */
public class RESTClient {
    private enum HttpVerb {
        POST, GET
    }

    public static final String ENDPOINT = "api.teamcowboy.com/v1/";

    private final IHttpProvider _httpProvider;
    private final MessageDigest sha1;
    private final Random _random = new Random(System.currentTimeMillis());
    private final String _publicApiKey;
    private final String _privateApiKey;
    private final SimpleDateFormat _dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson _gson;

    /**
     * @param publicApiKey your Team Cowboy public API key
     * @param privateApiKey your Team Cowboy private API key
     * @throws NoSuchAlgorithmException if SHA-1 is not supported by the runtime
     */
    public RESTClient(String publicApiKey, String privateApiKey) throws NoSuchAlgorithmException {
        this(publicApiKey, privateApiKey, new HttpProviderImpl());
    }
    
    /**
     * Constructor that allows for a test HTTP provider implementation.
     * 
     * @param publicApiKey your Team Cowboy public API key
     * @param privateApiKey your Team Cowboy private API key
     * @param httpProvider the IHttpProvider for the client to use
     * @throws NoSuchAlgorithmException if SHA-1 is not supported by the runtime
     */
    public RESTClient(String publicApiKey, String privateApiKey, IHttpProvider httpProvider) throws NoSuchAlgorithmException {
        _publicApiKey = publicApiKey;
        _privateApiKey = privateApiKey;
        sha1 = MessageDigest.getInstance("SHA-1");
        _httpProvider = httpProvider;

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CountByType.class, new CountByTypeDeserializer());
        builder.registerTypeAdapter(UserIdsByType.class, new UserIdsByTypeDeserializer());
        _gson = builder.create();
    }

    /**
     * Retrieves a user token for a Team Cowboy user account for use with your API account. User tokens are used and 
     * required for most other API methods. If a token does not yet exist for the API account/user pair, a new token 
     * will be created. Tokens cannot be retrieved for inactive user accounts. Additionally, if a user has disabled 
     * access from your API account/application, a token will not be returned, even if you have received one in the past.
     * 
     * @param username the username of the user you are getting a token for.
     * @param password the password of the user you are getting a token for.
     * @throws IOException
     * @see APIResponse
     */
    public APIResponse<UserInfo> Auth_GetUserToken(String username, String password) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        return call("Auth_GetUserToken", HttpVerb.POST, true, params, UserInfo.class);
    }

    /**
     * Retrieves details for a specific event. User must be an active team member on the team provided and the event 
     * must be associated with the team provided.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team that the event is associated with.
     * @param eventId id of the event to retrieve.
     * @param includeRSVPInfo whether or not to include RSVP information for the user (if null, server default value is used).
     * @throws IOException
     */
    public APIResponse<Event> Event_Get(String userToken, int teamId, int eventId, boolean includeRSVPInfo) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("eventId", eventId+"");
        params.put("includeRSVPInfo", includeRSVPInfo+"");
        return call("Event_Get", HttpVerb.GET, false, params, Event.class);
    }

    /**
     * Retrieves attendance list information for a specific event. This provides a list of team members and their RSVP 
     * statuses for the requested event.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team that the event is associated with.
     * @param eventId id of the event for the attendance list to retrieve.
     * @throws IOException
     */
    public APIResponse<AttendanceList> Event_GetAttendanceList(String userToken, int teamId, int eventId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("eventId", eventId+"");
        return call("Event_GetAttendanceList", HttpVerb.GET, false, params, AttendanceList.class);
    }

    /**
     * Saves an event RSVP for a user. Note that rules surrounding RSVPs are complex and can vary from event-level rules
     * to more complete team rules. These rules are taken into consideration when evaluating the parameter values below,
     * so some parameter values may be ignored. For example, if a team does not permit additional male or female players
     * from being included in RSVPs, these values, even if provided, will be ignored. Similarly, if a team does not allow
     * certain RSVP statuses such as "available", then providing "available" as the status parameter value will either
     * throw an error or it will default to the next best fit RSVP status (e.g., "maybe" or "no").
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team that the event is associated with.
     * @param eventId id of the event to save the RSVP for.
     * @param status the RSVP status to save for the user.  Valid values:  yes, maybe, available, no
     * @param addlMale optional. The number of additional male players to include as "yes" in the RSVP.
     * @param addlFemale optional. The number of additional female players to include as "yes" in the RSVP.
     * @param comments optional. RSVP comments. If not provided, any existing RSVP comments will be cleared out for the user's RSVP.
     * @param rsvpAsUserId optional. The user to RSVP for. This is used to allow a user to RSVP as a user that is in their list of linked users. If not provided, the RSVP will be saved for the user associated with the userToken parameter value.
     * @throws IOException
     */
    public APIResponse<SaveRSVPResponse> Event_SaveRSVP(String userToken, int teamId, int eventId, String status, Integer addlMale, Integer addlFemale, String comments, Integer rsvpAsUserId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("eventId", eventId+"");
        params.put("status", status);
        if (addlMale != null) params.put("addlMale", addlMale.toString());
        if (addlFemale != null) params.put("addlFemale", addlFemale.toString());
        if (comments != null) params.put("comments", comments);
        if (rsvpAsUserId != null) params.put("rsvpAsUserId", rsvpAsUserId.toString());
        return call("Event_SaveRSVP", HttpVerb.POST, false, params, SaveRSVPResponse.class);
    }

    /**
     * Retrieves information about a team message.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team associated with the message.
     * @param messageId id of the message to retrieve.
     * @param loadComments optional. Whether or not to load comments for the message. Default value: false
     * @throws IOException
     */
    public APIResponse<Message> Message_Get(String userToken, int teamId, int messageId, Boolean loadComments) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("messageId", messageId+"");
        if (loadComments != null) params.put("loadComments", loadComments.toString()); 
        return call("Message_Get", HttpVerb.GET, false, params, Message.class);
    }

    /**
     * Deletes a team message. The user attempting to delete the message must be a team admin for the team that the message 
     * is associated with, or they must be the author of the message.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team associated with the message.
     * @param messageId id of the message to delete.
     * @throws IOException
     */
    public APIResponse<Boolean> Message_Delete(String userToken, int teamId, int messageId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("messageId", messageId+"");
        return call("Message_Delete", HttpVerb.POST, false, params, Boolean.class);
    }

    /**
     * Saves (adds or updates) a team message.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team associated with the message to add/update.
     * @param messageId optional. id of the message to update. If not provided, a new message will be added.
     * @param title the title of the message.
     * @param body the body of the message. HTML is allowed, although unsafe tags are stripped out (nearly all normal HTML tags are allowed).
     * @param isPinned optional. Whether or not the message should be pinned in the team's list of messages. Can only be true for team admins. Default value: false.
     * @param sendNotifications optional. Whether or not to send notifications when the message and any message comments are posted. Default value: false.
     * @param isHidden optional. Whether or not the message is hidden in the team's list of messages. Can only be true for team admins. Default value: false.
     * @param allowComments optional. Whether or not comments can be posted for the message. Default value: true.
     * @throws IOException
     */
    public APIResponse<Message> Message_Save(String userToken, int teamId, Integer messageId, String title, String body, Boolean isPinned, Boolean sendNotifications, Boolean isHidden, Boolean allowComments) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        if (messageId != null) params.put("messageId", messageId.toString());
        params.put("title", title);
        params.put("body", body);
        if (isPinned != null) params.put("isPinned", isPinned.toString());
        if (sendNotifications != null) params.put("sendNotifications", sendNotifications.toString());
        if (isHidden != null) params.put("isHidden", isHidden.toString());
        if (allowComments != null) params.put("allowComments", allowComments.toString());
        return call("Message_Save", HttpVerb.POST, false, params, Message.class);
    }

    /**
     * Deletes a comment for a message. The user attempting to delete the comment must be a team admin for the team that the
     * message comment is associated with, or they must be the author of the comment.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team associated with the message.
     * @param messageId id of the message that the comment is associated with.
     * @param commentId id of the comment to delete.
     * @throws IOException
     */
    public APIResponse<Boolean> MessageComment_Delete(String userToken, int teamId, int messageId, int commentId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("messageId", messageId+"");
        params.put("commentId", commentId+"");
        return call("MessageComment_Delete", HttpVerb.POST, false, params, Boolean.class);
    }

    /**
     * Adds a new comment for a message. The message must allow comments to be posted or the request will not be successful.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team associated with the message.
     * @param messageId id of the message that the comment is associated with.
     * @param comment the text of the comment being added.
     * @throws IOException
     */
    public APIResponse<Boolean> MessageComment_Add(String userToken, int teamId, int messageId, String comment) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        params.put("messageId", messageId+"");
        params.put("comment", comment);
        return call("MessageComment_Add", HttpVerb.POST, false, params, Boolean.class);
    }

    /**
     * Retrieves information about a team. The team requested must be accessible by the user represented by the user token
     * being provided (i.e., you cannot retrieve team information for a team unless the user is an active member of that team).
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team to retrieve.
     * @throws IOException
     */
    public APIResponse<Team> Team_Get(String userToken, int teamId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        return call("Team_Get", HttpVerb.GET, false, params, Team.class);
    }

    /**
     * Retrieves an array of events for a team's season.The team requested must be accessible by the user represented by the
     * user token being provided (i.e., you cannot retrieve team information for a team unless the user is an active member of
     * that team).
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team to retrieve events for.
     * @param seasonId optional. id of the season to retrieve events for. If not provided, events for all of the team's seasons will be returned.
     * @param filter optional. an enumeration value indicating a special filter for retrieving events. Valid values: past, future, specificDates, nextEvent, previousEvent. Default value: future.
     * @param startDateTime required if filter=specificDates. If provided, events will only be retrieved that have a start date on or after this value. The value provided is evaluated against the local date for the event. Enter date/time in format: YYYY-MM-DD HH:MM:SS
     * @param endDateTime required if filter=specificDates. If provided, events will only be retrieved that have a end date on or before this value. The value provided is evaluated against the local date for the event. Enter date/time in format: YYYY-MM-DD HH:MM:SS
     * @param offset optional. The number of events to shift from those returned. This is typically used if you are requesting a specific number of events per page and you need to offset to a different page. This value is zero-based (i.e., for no offset, use 0, not 1). Default value: 0
     * @param qty optional. The number of events to retrieve. Again, if using pagination in your application, this would typically be the page size, or you just want to reduce the response size (i.e., less events). Default value: 10
     * @throws IOException
     */
    public APIResponse<Event[]> Team_GetEvents(String userToken, int teamId, Integer seasonId, String filter, Date startDateTime, Date endDateTime, Integer offset, Integer qty) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        if (seasonId != null) params.put("seasonId", seasonId.toString());
        if (filter != null) params.put("filter", filter);
        if (startDateTime != null) params.put("startDateTime", _dateFormatter.format(startDateTime));
        if (endDateTime != null) params.put("endDateTime", _dateFormatter.format(endDateTime));
        if (offset != null) params.put("offset", offset.toString());
        if (qty != null) params.put("qty", qty.toString());
        return call("Team_GetEvents", HttpVerb.GET, false, params, Event[].class);
    }

    /**
     * Retrieves an array of Message Board posts for a specific team.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team to retrieve messages for.
     * @param offset optional. The number of messages to shift from those returned. This is typically used if you are requesting a specific number of messages per page and you need to offset to a different page. This value is zero-based (i.e., for no offset, use 0, not 1). Default value: 0
     * @param qty optional. The number of messages to retrieve. Again, if using pagination in your application, this would typically be the page size. Default value: 10
     * @param sortBy optional. An enumeration value indicating how to sort the messages that are returned. Valid values: title, lastUpdated, type. Default value: lastUpdated
     * @param sortDirection optional. The sort direction for the messages returned. Valid values:  ASC, DESC. Default value: The default value varies based on the sortBy parameter.
     * @throws IOException
     */
    public APIResponse<Message[]> Team_GetMessages(String userToken, int teamId, Integer offset, Integer qty, String sortBy, String sortDirection) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        if (offset != null) params.put("offset", offset.toString());
        if (qty != null) params.put("qty", qty.toString());
        if (sortBy != null) params.put("sortBy", sortBy);
        if (sortDirection != null) params.put("sortDirection", sortDirection);
        return call("Team_GetMessages", HttpVerb.GET, false, params, Message[].class);
    }

    /**
     * Retrieves roster members for a given team.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team to retrieve.
     * @param userId optional. Id of a specific user/team member to retrieve. If omitted, all team members are returned.
     * @param includeInactive optional. Whether or not to include team members that are marked as "inactive" for a team (inactive team members cannot access the team but are visible from the Roster page for team admins). Default value: true
     * @param sortBy optional. Order to sort the team members returned. The valid values for this parameter vary depending on whether or not the user making the method call is an admin on the team. Valid values (case-sensitive): If user is a team admin:  playerType, playerType_sex, sex, sex_playerType, email, email2, firstName, lastName, phone, tshirtSize, tshirtNumber, pantsSize, lastLogin, active, inviteStatus. If user is not a team admin: firstName, playerType, sex. Default value: firstName
     * @param sortDirection Optional. The sort direction for the team members returned. Valid values: ASC, DESC. Default value: ASC
     * @throws IOException
     */
    public APIResponse<User> Team_GetRoster(String userToken, int teamId, Integer userId, Boolean includeInactive, String sortBy, String sortDirection) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        if (userId != null) params.put("userId", userId.toString());
        if (includeInactive != null) params.put("includeInactive", includeInactive.toString());
        if (sortBy != null) params.put("sortBy", sortBy);
        if (sortDirection != null) params.put("sortDirection", sortDirection);
        return call("Team_GetRoster", HttpVerb.GET, false, params, User.class);
    }

    /**
     * Retrieves schedule seasons for a team. The team requested must be accessible by the user represented by the user token
     * being provided (i.e., you cannot retrieve team information for a team unless the user is an active member of that team).
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId id of the team to retrieve seasons for.
     * @throws IOException
     */
    public APIResponse<Season[]> Team_GetSeasons(String userToken, int teamId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        params.put("teamId", teamId+"");
        return call("Team_GetSeasons", HttpVerb.GET, false, params, Season[].class);
    }

    public APIResponse<String> Test_GetRequest(String testParam) throws IOException {
        return Test_Request(testParam, HttpVerb.GET);
    }

    public APIResponse<String> Test_PostRequest(String testParam) throws IOException {
        return Test_Request(testParam, HttpVerb.POST);
    }

    private APIResponse<String> Test_Request(String testParam, HttpVerb verb) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        if (testParam != null) params.put("testParam", testParam);
        return call("Test_PostRequest", verb, false, params, String.class);
    }

    /**
     * Retrieves user details.
     * 
     * @param userToken API account/user token for the user to retrieve information for.
     * @throws IOException
     */
    public APIResponse<User> User_Get(String userToken) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        return call("User_Get", HttpVerb.GET, false, params, User.class);
    }

    /**
     * Retrieves the next event on the user’s event schedule. By default, the event will be the next event from any of the
     * teams that are visible in the user’s profile. The next event can be restricted to a specific team by providing a value
     * for the teamId parameter in the method call.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId optional. A teamId to restrict the event returned to a specific team. If not provided, events for all of the user's teams will be considered.
     * @throws IOException
     */
    public APIResponse<Event> User_GetNextTeamEvent(String userToken, Integer teamId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        if (teamId != null) params.put("teamId", teamId.toString());
        return call("User_GetNextTeamEvent", HttpVerb.GET, false, params, Event.class);
    }

    /**
     * Retrieves an array of events for the teams that the user is an active member of. Events are only returned for teams
     * that are visible in the user's profile.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param startDateTime optional. If provided, events will only be retrieved that have a start date/time on or after this value. The value provided is evaluated against the local date/time for the event. If not provided, the current date/time is used. Enter date/time in format: YYYY-MM-DD HH:MM:SS
     * @param endDateTime optional. If provided, events will only be retrieved that have a start date/time on or before this value. The value provided is evaluated against the local date/time for the event. If not provided, the current date/time + 60 days is used. Enter date/time in format: YYYY-MM-DD HH:MM:SS
     * @param teamId optional. A teamId to restrict the events returned to a specific team. If not provided, events for all of the user's teams are returned.
     * @throws IOException
     */
    public APIResponse<Event[]> User_GetTeamEvents(String userToken, Date startDateTime, Date endDateTime, Integer teamId) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        if (startDateTime != null) params.put("startDateTime", _dateFormatter.format(startDateTime));
        if (endDateTime != null) params.put("endDateTime", _dateFormatter.format(endDateTime));
        if (teamId != null) params.put("teamId", teamId.toString());
        return call("User_GetTeamEvents", HttpVerb.GET, false, params, Event[].class);
    }
    
    /**
     * Retrieves an array of Message Board posts for the teams that the user is an active member of. Message Board posts are
     * only returned for teams that are visible in the user's profile.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @param teamId optional. A teamId to restrict the messages that are returned to a specific team. Default: If this value is not provided, messages for all of the user's teams are returned.
     * @param offset optional. The number of messages to shift from those returned. This is typically used if you are requesting a specific number of messages per page and you need to offset to a different page. This value is zero-based (i.e., for no offset, use 0, not 1). Default value: 0
     * @param qty optional. The number of messages to retrieve. Again, if using pagination in your application, this would typically be the page size. Default value: 10
     * @param sortBy optional. An enumeration value indicating how to sort the messages that are returned. Valid values: title, lastUpdated, type. Default value: lastUpdated
     * @param sortDirection optional. The sort direction for the messages returned. Valid values:  ASC, DESC. Default value: The default value varies based on the sortBy parameter.
     * @throws IOException
     */
    public APIResponse<Message[]> User_GetTeamMessages(String userToken, Integer teamId, Integer offset, Integer qty, String sortBy, String sortDirection) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        if (teamId != null) params.put("teamId", teamId.toString());
        if (offset != null) params.put("offset", offset.toString());
        if (qty != null) params.put("qty", qty.toString());
        if (sortBy != null) params.put("sortBy", sortBy);
        if (sortDirection != null) params.put("sortDirection", sortDirection);
        return call("User_GetTeamMessages", HttpVerb.GET, false, params, Message[].class);
    }

    /**
     * Retrieves an array of teams that the user is an active member of.
     * 
     * @param userToken API account/user token. See {@link RESTClient#Auth_GetUserToken(String, String)}.
     * @throws IOException
     */
    public APIResponse<Team[]> User_GetTeams(String userToken) throws IOException {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userToken", userToken);
        return call("User_GetTeams", HttpVerb.GET, false, params, Team[].class);
    }

    /**
     * Private helper method to in
     * 
     * @param method the method to call
     * @param httpVerb the HTTP verb to use in the request
     * @param secure whether the request should be done over HTTPS
     * @param params the list of parameters to the method
     * @param clazz the expected return type for the request 
     * @return the wrapped response to the request
     */
    private <T> APIResponse<T> call(String method, HttpVerb httpVerb, boolean secure, TreeMap<String, String> params, Class<T> clazz) throws IOException {
        String response = invoke(method, httpVerb, secure, params);
        
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(response).getAsJsonObject();
        boolean success = result.get("success").getAsBoolean();
        Number requestSecs = result.get("requestSecs").getAsNumber();
        
        synchronized (_gson) {
            return new APIResponse<T>(success, requestSecs,
                    success ? _gson.fromJson(result.get("body"), clazz) : null,
                    success ? null : _gson.fromJson(result.get("body"), APIError.class));
        }
    }
    
    /**
     * Invokes a Team Cowboy REST method.
     * 
     * @param method the method to call
     * @param httpVerb the HTTP verb to use in the request
     * @param secure whether the request should be done over HTTPS
     * @param params the list of parameters to the method
     * @return the body of the HTTP response
     */
    private String invoke(String method, HttpVerb httpVerb, boolean secure, TreeMap<String, String> params) throws IOException {
        String paramString = makeHttpParamString(httpVerb, method, params);
        
        switch (httpVerb) {
        case GET:
            return _httpProvider.makeHTTPCall((secure ? "https://" : "http://") + ENDPOINT + "?" + paramString, "", httpVerb.toString());
        case POST:
            return _httpProvider.makeHTTPCall((secure ? "https://" : "http://") + ENDPOINT, paramString, httpVerb.toString());
        default:
            return null;
        }
    }

    /**
     * Returns the parameter string for a given method call and set of parameters.
     *  
     * @param httpVerb the HTTP verb to use
     * @param method
     * @param params
     */
    private String makeHttpParamString(HttpVerb httpVerb, String method, TreeMap<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        String timestamp = (System.currentTimeMillis()/1000)+"";
        String nonce = timestamp + _random.nextInt(99);
        StringBuffer paramsBuffer = new StringBuffer();
        
        //If you're interested in debugging the nonce:
        //System.out.println("Method: " + method + ", Nonce: " + nonce);

        params.put("api_key", _publicApiKey);
        params.put("method", method);
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);
        params.put("response_type", "json");

        appendParams(paramsBuffer, params, true, false);
        buffer.append(paramsBuffer.toString());
        
        String sigInput = _privateApiKey + "|" + httpVerb.toString() + "|" + method + "|" + timestamp + "|" + nonce + "|" + paramsBuffer.toString().toLowerCase(); 
        sha1.update(sigInput.getBytes());
        appendParam(buffer, "sig", new BigInteger(1, sha1.digest()).toString(16).toLowerCase(), false, true);
        sha1.reset();
        
        return buffer.toString();
    }

    /**
     * Appends a sorted map of name/value pairs in HTTP parameter format to a string buffer.
     * 
     * @param buffer the StringBuffer to append to
     * @param params the map of name value pairs to append
     * @param urlEncoded whether to URL encode the value or not
     * @param ampersandPrefix whether to prepend an ampersand before the name/value pair
     */
    private void appendParams(StringBuffer buffer, TreeMap<String, String> params, boolean urlEncode, boolean ampersandPrefix) {
        if (params == null || params.size() == 0)
            return;

        boolean isFirst = true;
        for (Entry<String, String> key : params.entrySet()) {
            appendParam(buffer, key.getKey(), key.getValue(), urlEncode, !isFirst || ampersandPrefix);
            isFirst = false;
        }
    }

    /**
     * Appends a parameter in HTTP parameter format to string buffer.
     * 
     * @param buffer the StringBuffer to append to
     * @param name the name of the parameter
     * @param value the value for the parameter
     * @param urlEncode whether to URL encode the value or not
     * @param ampersandPrefix whether to prepend an ampersand before the name/value pair
     */
    private void appendParam(StringBuffer buffer, String name, String value, boolean urlEncode, boolean ampersandPrefix) {
        if (ampersandPrefix) buffer.append("&");
        if (urlEncode) {
            try {
                value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
            } catch (UnsupportedEncodingException uee) {
                //Should never happen as UTF-8 should always be supported.
                throw new RuntimeException(uee);
            }
        }
        buffer.append(name).append("=").append(value);
    }
}