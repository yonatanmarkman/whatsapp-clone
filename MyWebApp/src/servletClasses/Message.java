package servletClasses;

import java.util.Calendar;

/**
 * 
 * The Message object stores data needed to represent a message properly in the conversation, s.t. all
 * users can see it and reply to it (even if it's already a reply).
 * It uses the calendar object to calculate the sent time at the server side.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class Message {
	private int messageId; //Identifier of the message.
	private int channelId; //Identifier of the channel.
	private String userName; //Username of the sender.
	private String nickName; //Nickname of the sender.
	private String photo; //Profile photo of the sender.
	private Calendar sentTime; //Timestamp reperesenting the time the message was sent.
	private String content; //Content of the message.
	private int parentMsgId; //Identifier of the parent message (in case the current message is a reply);
	
	/* basic constructor */

	public Message(int messageId, int channelId, String userName, String nickName, String photo, Calendar sentTime,
			String content, int parentMsgID) {
		this.messageId = messageId;
		this.channelId = channelId;
		this.userName = userName;
		this.nickName = nickName;
		this.photo = photo;
		this.sentTime = sentTime;
		this.content = content;
		this.parentMsgId = parentMsgID;
	}

	/* basic getters */
	
	public int getMessageId() {
		return messageId;
	}

	public int getChannelId() {
		return channelId;
	}

	public String getUserName() {
		return userName;
	}

	public String getNickName() {
		return nickName;
	}

	public String getPhoto() {
		return photo;
	}

	public Calendar getSentTime() {
		return sentTime;
	}

	public String getContent() {
		return content;
	}

	public int getParentMsgId() {
		return parentMsgId;
	}

	public String toString() {
		return "toString(): [ msgId: " + "" + messageId + ", chnlId:" + channelId + ", username:" + "" + userName
				+ ", content:" + " " + content + ", parentMsgId:" + parentMsgId + " ]";
	}
}