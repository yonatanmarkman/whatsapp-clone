package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.SubDetails;
import servletClasses.UserChannel;

/**
 * Servlet for Subscribe requests
 * 
 * @author Sagi
 *
 */
@WebServlet(description = "", urlPatterns = { "/Subscribe" })
public class SubscribeServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	public SubscribeServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("subscription : ");
		String js = getRequestData(request, response);
		boolean flag = addSubscriptionToUserChannels(request, response, js);
		int status = 0; // success in subscribing
		if (!flag)
			status = 1;

		String responseToSend = (" { \"status\": \"" + status + "\" } ");
		super.sendData(response, responseToSend);
	}
	
	/**
	 * Add record to the userChannel table
	 * @param request
	 * @param response
	 * @param js
	 * @return true
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean addSubscriptionToUserChannels(HttpServletRequest request, HttpServletResponse response, String js)
			throws ServletException, IOException {
		SubDetails subDetails = parseJsonString(js).get(0);
		int channelId = subDetails.getChannelId();
		String username = subDetails.getUsername();
		// get current time and date, and create new userchannel object using
		// sub details;

		// change this later!!! this here is an errrrrrrrrrrrrrrrr
		Calendar enterTime = Calendar.getInstance();
		Calendar exitTime = enterTime;
		Calendar subscriptionTime = enterTime;

		UserChannel subscription = new UserChannel(channelId, username, enterTime, exitTime, subscriptionTime);

		// add other stuff as well when it's the time.
		System.out.println("sub username: " + username);
		System.out.println("sub channel id: " + channelId);
		System.out.println("sub enter time: " + enterTime);
		System.out.println("sub exit time: " + exitTime);
		System.out.println("sub time: " + subscriptionTime);

		// loading database stuff..

		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			// insert new userchannel record.
			try {
				stmt = conn.prepareStatement(AppConstants.INSERT_USERCHANNEL_STMT);
				stmt.setInt(1, channelId);
				stmt.setString(2, username);
				stmt.setTimestamp(3, new Timestamp(enterTime.getTimeInMillis()));
				stmt.setTimestamp(4, new Timestamp(exitTime.getTimeInMillis()));
				stmt.setTimestamp(5, new Timestamp(subscriptionTime.getTimeInMillis()));
				stmt.executeUpdate();
				stmt.close();
				System.out.println("new subscription added");
			} catch (SQLException e) {
				getServletContext().log("Error while querying for users", e);
				response.sendError(500);// internal server error
			}
			conn.close();
		} catch (SQLException e) {
			getServletContext().log("Error while querying for users", e);
			response.sendError(500);// internal server error
		}

		return true;
	}

	/**
	 * Method to parse json string to a List<SubDetails> object
	 *
	 * @param jsonLine
	 * 			the json string containing the info about the List<SubDetails> to be created.
	 * @return the List<SubDetails> object parsed from the jsonLine.
	 * 			
	 */
	private List<SubDetails> parseJsonString(String jsonLine) {
		Type listType = new TypeToken<List<SubDetails>>() {
		}.getType();
		Gson gson = new Gson();
		String json = "[" + jsonLine + "]"; // gson.toJson(target, listType);
		System.out.println("jsonLine: " + jsonLine);
		List<SubDetails> target2 = gson.fromJson(json, listType);
		return target2;
	}
}
