package servletClasses;

import java.util.Calendar;

/**
 * 
 * The SubDetails object is used to send Subscription data via the JSON format between server-side and client-side,
 * without the timestamps.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class SubDetails {
	private int channelId; //Identifier of the channel related to the subscription.
	private String username; //Identifier of the username related to the subscription.
	
	/* basic constructor */

	public SubDetails(int channelId, String username) {
		this.channelId = channelId;
		this.username = username;
	}
	
	/* basic getters */

	public int getChannelId() {
		return channelId;
	}

	public String getUsername() {
		return username;
	}

	public String toString() {
		return "toString(): " + "[ chanId:" + channelId + ", uname: " + username + " ]";
	}
}
