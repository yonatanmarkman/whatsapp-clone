package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import servletClasses.Message;
import servletClasses.UserChannel;
import servlets.AppConstants;

/**
 * The GetMessagesServlet is used to get all messages that belong to a given channel.
 * 
 * @author YONATAN
 * @since 01-03-2017
 *
 **/
@WebServlet(description = "", urlPatterns = { "/GetMessages/*", "/GetMessages/channelId/*", "/GetMessages/channelId/*/username/*" })
public class GetMessagesServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public GetMessagesServlet() {
		super();
	}

	/**
	 * doGet method to get the messages from the database.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//getting the client input data from the url
		String c = "channelId";
		String channelId = getRequestDataFromUrl(request, response, c);
		channelId = channelId.substring(0, channelId.indexOf('/'));
		String c2 = "username";
		String username = getRequestDataFromUrl(request, response, c2);

		try {
			
			// obtain db data source from Tomcat's context
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			PreparedStatement stmt;

			// get the right subscription from the database.
			ArrayList<UserChannel> subscriptionList = new ArrayList<UserChannel>();
			Calendar enterCal = Calendar.getInstance();
			Calendar exitCal = Calendar.getInstance();
			Calendar subCal = Calendar.getInstance();
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_USERCHANNEL_BY_ID_AND_USERNAME_STMT);
				stmt.setInt(1, Integer.parseInt(channelId));
				stmt.setString(2, username);

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					enterCal.setTimeInMillis(rs.getTimestamp(3).getTime());
					exitCal.setTimeInMillis(rs.getTimestamp(4).getTime());
					subCal.setTimeInMillis(rs.getTimestamp(5).getTime());
					subscriptionList.add(new UserChannel(rs.getInt(1), rs.getString(2), enterCal, exitCal, subCal));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for userChannels", e);
				response.sendError(500);// internal server error
			}

			// get all messages from current channel,by the subscription
			// s.t. newely subscribed users to channels will see only messages after their time of subscription.

			Collection<Message> messagesResult = new ArrayList<Message>();
			Calendar sentCal = Calendar.getInstance();
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_ID_AND_SUBSCRIPTION_STMT);
				stmt.setString(1, channelId);
				stmt.setTimestamp(2, new Timestamp(subCal.getTimeInMillis()));

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					
					// getting the message timestamp
					sentCal.setTimeInMillis(rs.getTimestamp(6).getTime());
					
					// display messages that are relevant only to our channel and user.
					messagesResult.add(new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
							rs.getString(5), sentCal, rs.getString(7), rs.getInt(8)));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for messages", e);
				response.sendError(500);// internal server error
			}

			conn.close();

			Gson gson = new Gson();
			
			// convert from messages collection to json
			String messageJsonResult = gson.toJson(messagesResult, AppConstants.MESSAGE_COLLECTION);
			PrintWriter writer = response.getWriter();
			writer.println(messageJsonResult);
			writer.close();
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);// internal server error
		}
	}

	/**
	 * Basic doPost method.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException		
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost");
	}
}
