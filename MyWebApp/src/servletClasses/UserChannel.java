package servletClasses;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 
 * The UserChannel object stores the subscription data of a user, including the moment he subscribed to the channel.
 * It also stores the last times he entered/exited the channel to which he is subscribed.
 * All Calendar objects represent timestamps calculated at server side.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class UserChannel {
	private int channelId; //Identifier of the channel related to the subscription.
	private String username; //Identifier of the username related to the subscription.
	private Calendar enterTime; //Timestamp representing the last time the user entered the channel.
	private Calendar exitTime; //Timestamp representing the last time the user exited the channel.
	private Calendar subsciptionTime; //Timestamp representing when the user subscribed to the channel.

	/* basic constructor */
	
	public UserChannel(int channelId, String username, Calendar enterTime, Calendar exitTime,
			Calendar subsciptionTime) {
		this.channelId = channelId;
		this.username = username;
		this.enterTime = enterTime;
		this.exitTime = exitTime;
		this.subsciptionTime = subsciptionTime;
	}

	/* basic getters */
	
	public int getChannelId() {
		return channelId;
	}

	public String getUsername() {
		return username;
	}

	public Calendar getEnterTime() {
		return enterTime;
	}

	public Calendar getExitTime() {
		return exitTime;
	}

	public Calendar getSubsciptionTime() {
		return subsciptionTime;
	}

	public String toString() {
		return "toString(): " + " [ chanId:" + channelId + ", uname: " + username + "]";
	}
}