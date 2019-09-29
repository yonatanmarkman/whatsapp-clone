package servletClasses;

import java.util.Calendar;

/**
 * 
 * The MsgDetails object is used to send Message data via the JSON format between server-side and client-side.
 * We use this object because the Message object has a timestamp parameter which is very problematic to calculate at client-side.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class MsgDetails {
	private int messageId; //Identifier of the message.
	private int channelId; //Identifier of the channel.
	private String userName; //Username of the sender.
	private String nickName; //Nickname of the sender.
	private String photo; //Profile photo of the sender.
	private String content; //Content of the message.
	private int parentMsgId; //Identifier of the parent message (in case the current message is a reply);
	
	/* basic constructor */

	public MsgDetails(int messageId, int channelId, String userName, String nickName, String photoName, String content,
			int parentMsgID) {
		this.messageId = messageId;
		this.channelId = channelId;
		this.userName = userName;
		this.nickName = nickName;
		this.photo = photoName;
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

	public String getNickName() {
		return nickName;
	}

	public String getPhoto() {
		return photo;
	}

	public String getUserName() {
		return userName;
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
