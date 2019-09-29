package webSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


/**
 * Web Socket class used to chat dynamically between users.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

@ServerEndpoint("/chat/{username}/{channel}")
public class ChatEndPoint {

	// tracks all active chat users
	private static Map<String, ArrayList<LoggedInUser>> map = Collections
			.synchronizedMap(new HashMap<String, ArrayList<LoggedInUser>>());

	/**
	 * Joins a new client to the chat
	 * 
	 * @param session
	 *            client end point session
	 */
	@OnOpen
	public void joinChat(Session session, @PathParam("username") String username,
			@PathParam("channel") String channel) {
			if (session.isOpen()) {
				System.out.println("websocket : joinChat\nsession: " + session);
				ArrayList<LoggedInUser> currentParticipants = map.get(channel);
				if (currentParticipants == null) {
					currentParticipants = new ArrayList<LoggedInUser>();
					currentParticipants.add(new LoggedInUser(username, session));
					map.put(channel, currentParticipants);
					System.out.println(">>>>>> User " + username + " opened new discussion on channel " + channel);
				} else {
					currentParticipants.add(new LoggedInUser(username, session));
					System.out.println(">>>>>> User " + username + " joined on channel " + channel);
				}
				System.out.println(
						">>>>>> There are currently " + currentParticipants.size() + " people on channel " + channel);

				System.out.println(">>>> and they are: " + currentParticipants);
			}
	}

	/**
	 * Message delivery between chat participants
	 * 
	 * @param session
	 *            client end point session
	 * @param messageToSend
	 *            message to deliver
	 */
	@OnMessage
	public void deliverChatMessege(Session session, String messageToSend, @PathParam("channel") String channel){
		System.out.println("websocket : deliverChatMessege");
		if (session.isOpen()) {
			// deliver message
			doNotify(messageToSend, channel);
		}
	}

	/**
	 * Removes a client from the chat
	 * 
	 * @param session
	 *            client end point session
	 */
	@OnClose
	public void leaveChat(Session session, @PathParam("username") String username, @PathParam("channel") String channel){
		System.out.println(">>>leaveChat<<<");
		ArrayList<LoggedInUser> currentParticipants = map.get(channel);
		for (LoggedInUser user : currentParticipants) {
			if (username.equals(user.username)) {
				currentParticipants.remove(user);
				break;
			}
		}

		if (currentParticipants.size() == 0)
			map.remove(channel);
		System.out.println(">>User " + username + " has left the chat...");
		System.out.println(">!>!>!!>!>!>! currently left at " + channel + " are: " + currentParticipants);
	}


	/**
	 * Function to send message to all participants in the channel.
	 * 
	 * @param message
	 * 			the message to send.
	 * 		  channel
	 * 			the channel to send the message to.
	 **/
	private void doNotify(String message, String channel){
		ArrayList<LoggedInUser> currentParticipants = map.get(channel);
		for (LoggedInUser user : currentParticipants) {
			Session session = user.session;
			if (session.isOpen()) {
				try {
					session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
