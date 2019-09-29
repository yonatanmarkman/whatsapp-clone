package webSocket;

import javax.websocket.Session;

/**
 * Class used to store session details per user.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class LoggedInUser {
	public String username; //Username string
	public Session session;	//Session identifier.

	public LoggedInUser(String username, Session session) {
		this.username = username;
		this.session = session;
	}

	public String toString() {
		return "{" + username + "}";
	}
}
