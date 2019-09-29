package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.Channel;

/**
 * The CreateChannelServlet is used to create a new channel via http post.
 * 
 * @author YONATAN
 * @since 01-03-2017
 */

@WebServlet("/CreateChannel")
public class CreateChannelServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public CreateChannelServlet() {
		super();
	}

	/**
	 * Basic doGet method.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//super.doGet(request, response);
	}

	/**
	 * doPost method to add a new channel to the database.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Getting the request data from the http requst.
		String js = getRequestData(request, response);
		
		//Getting the channel object form the json string
		Channel newChannel = parseJsonString(js);
		String channelName = newChannel.getChannelName();
		int newChannelId = addNewChannelToDatabase(request, response, js);
		newChannel.setChannelId(newChannelId);
		
		// sending the status about the new channel that was created
		String responseToSend = " { \"newChannelId\": \"" + newChannelId + "\", \"newChannelName\": \"" + channelName
				+ "\" } ";
		super.sendData(response, responseToSend);
	}

	/**
	 * Method to get channel object from json string.
	 *
	 * @param jsonLine
	 * 			the request from the client side
	 * 		  request
	 * 			the response to contact the client side
	 * 			
	 */
	private Channel getChannelFromJsonLine(String jsonLine) {
		Channel newChannel = parseJsonString(jsonLine);
		int channelId = newChannel.getChannelId();
		String channelName = newChannel.getChannelName();
		String channelDescription = newChannel.getChannelDescription();
		String type = newChannel.getType();
		return newChannel;
	}

	/**
	 * Method to add a new channel to the database
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * 		  jsonLine
	 * 			the json string containing the information from the client.
	 * @return the channel id of the newely added channel.
	 * @throws ServletException, IOException
	 */
	private int addNewChannelToDatabase(HttpServletRequest request, HttpServletResponse response, String jsonLine)
			throws ServletException, IOException {
		Channel newChannel = getChannelFromJsonLine(jsonLine);
		int channelId = 0;
		String channelName = newChannel.getChannelName();
		String channelDescription = newChannel.getChannelDescription();
		String type = newChannel.getType();

		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmtGetKey;
			try {
				stmtGetKey = conn.prepareStatement(AppConstants.INSERT_CHANNEL_STMT, Statement.RETURN_GENERATED_KEYS);
				stmtGetKey.setString(1, channelName);
				stmtGetKey.setString(2, channelDescription);
				stmtGetKey.setString(3, type);
				stmtGetKey.executeUpdate();

				try (ResultSet generatedKeys = stmtGetKey.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						channelId = (int) generatedKeys.getLong(1);
					} else {
						throw new SQLException("Creating channel failed, no ID obtained.");
					}
				}

				stmtGetKey.close();
			} catch (SQLException e) {
				getServletContext().log("Error while creating channel.", e);
				response.sendError(500);// internal server error
			}
			conn.close();
		} catch (SQLException e) {
			getServletContext().log("Error while creating channel.", e);
			response.sendError(500);// internal server error
		}
		return channelId;
	}

	/**
	 * Method to parse json string to a channel object
	 *
	 * @param jsonLine
	 * 			the json string containing the info about the channel to be created.
	 * @return the channel object parsed from the jsonLine.
	 * 			
	 */
	private Channel parseJsonString(String jsonLine) {
		Type listType = new TypeToken<Channel>() {
		}.getType();
		Gson gson = new Gson();
		String json = jsonLine; // gson.toJson(target, listType);
		Channel target2 = gson.fromJson(json, listType);
		return target2;
	}

}
