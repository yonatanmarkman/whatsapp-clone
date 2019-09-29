package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.UserChannel;

/**
 * Servlet for Subscribe requests
 * 
 * @author Sagi
 *
 */
@WebServlet("/UnSubscribe")
public class UnsubscribeServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	public UnsubscribeServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("UNsubscription : ");
		String js = getRequestData(request, response);
		boolean flag = unSub(request, response, js);
		int status = 0; // success in subscribing
		if (!flag)
			status = 1;

		String responseToSend = (" { \"status\": \"" + status + "\" } ");
		super.sendData(response, responseToSend);
	}

	/**
	 * Deletes the record that describes in the request from the userChannel table
	 * @param request
	 * @param response
	 * @param js
	 * @return true
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean unSub(HttpServletRequest request, HttpServletResponse response, String js)
			throws ServletException, IOException {
		UserChannel subscription = parseJsonString(js).get(0);
		int channelId = subscription.getChannelId();
		String username = subscription.getUsername();
		// change this later!!! this here is an errorrrrr

		// add other stuff as well when it's the time.
		System.out.println("sub username: " + username);
		System.out.println("sub channel id: " + channelId);

		// loading database stuff..

		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.DELETE_USERCHANNEL_STMT);
				stmt.setInt(1, channelId);
				stmt.setString(2, username);
				stmt.executeUpdate();
				stmt.close();
				System.out.println("unsub successful");
			} catch (SQLException e) {
				getServletContext().log("Error while unsubbing.", e);
				response.sendError(500);// internal server error
			}
			conn.close();
		} catch (SQLException e) {
			getServletContext().log("Error while unsubbing.", e);
			response.sendError(500);// internal server error
		}

		return true;
	}

	/**
	 * Method to parse json string to a List<UserChannel> object
	 *
	 * @param jsonLine
	 * 			the json string containing the info about the List<UserChannel> to be created.
	 * @return the List<UserChannel> object parsed from the jsonLine.
	 * 			
	 */
	private List<UserChannel> parseJsonString(String jsonLine) {
		Type listType = new TypeToken<List<UserChannel>>() {
		}.getType();
		Gson gson = new Gson();
		String json = "[" + jsonLine + "]"; // gson.toJson(target, listType);
		List<UserChannel> target2 = gson.fromJson(json, listType);
		return target2;
	}
}
