package servletClasses;

/**
 * The Channel object stores data needed to access a channel, and to maintain a conversation of
 * a group of users.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class Channel {
	private int channelId; //Identifier of the channel
	private String channelName; //Name of the channel
	private String channelDescription; //Short description of the channel
	private String type; //Type of channel - private or public
	private int mentions = 0; //integer used to send channel object via json, always zero in java side.
	private int notifications = 0; //integer used to send channel object via json, always zero in java side.
	
	/* Basic constuctors */
	
	public Channel(int channelId, String channelName, String channelDescription, String type) {
		this.channelId = channelId;
		this.channelName = channelName;
		this.channelDescription = channelDescription;
		this.type = type;
	}

	public Channel() {
		//empty constructor
	}
	
	/*basic getters and setters*/

	public int getChannelId() {
		return channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String cName) {
		channelName = cName;
	}

	public void setChannelId(int cId) {
		channelId = cId;
	}

	public String getChannelDescription() {
		return channelDescription;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return "toString() : [ chanId: " + channelId + ", chanName: " + channelName + ", chanDesc: "
				+ channelDescription + ", chaType: " + type + " ]"+mentions+notifications;
	}
}
