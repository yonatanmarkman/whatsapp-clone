package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.MsgDetails;

/**
 * Servlet for sending message request
 * 
 * @author Sagi
 *
 */
@WebServlet(description = "Servlet to provide details about sign up", urlPatterns = { "/SendMessage" })
public class SendMessageServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	public SendMessageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String jb = getRequestData(request, response);
		this.addNewMessage(request, response, jb);
		int status = 0;
		String responseToSend = (" { \"status\": \"" + status + "\" } ");
		super.sendData(response, responseToSend);
	}

	/**
	 * Helper function - Insert the message to the database
	 * @param request the request that includes all the  details about the message
	 * @param response the object which uses to send back the message
	 * @param jsonLine message content
	 * @throws ServletException
	 * @throws IOException
	 */
	private void addNewMessage(HttpServletRequest request, HttpServletResponse response, String jsonLine)
			throws ServletException, IOException {
		// accessing database;
		MsgDetails msgDetails = parseJsonString(jsonLine).get(0);

		int messageId = msgDetails.getMessageId();
		int channelId = msgDetails.getChannelId();
		String userName = msgDetails.getUserName();
		String nickName = msgDetails.getNickName();
		String photo = msgDetails.getPhoto();
		Calendar sentTime = Calendar.getInstance();
		String content = msgDetails.getContent();
		int parentMsgId = msgDetails.getParentMsgId();

		// loading database stuff..
		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT);
				// stmt.setInt(1, messageId);
				stmt.setInt(1, channelId);
				stmt.setString(2, userName);
				stmt.setString(3, nickName);
				stmt.setString(4, photo);
				stmt.setTimestamp(5, new java.sql.Timestamp(sentTime.getTimeInMillis()));
				System.out.println("stmt.setTimestamp(5, new java.sql.Timestamp(sentTime.getTimeInMillis()));"+ 
						new java.sql.Timestamp(sentTime.getTimeInMillis()));
				stmt.setString(6, content);
				stmt.setInt(7, parentMsgId);
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while sending msg", e);
				response.sendError(500);// internal server error
			}
			conn.close();
		} catch (SQLException e) {
			getServletContext().log("Error while sending msg", e);
			response.sendError(500); // internal server error
		}
	}


	/**
	 * Method to parse json string to a List<MsgDetails> object
	 *
	 * @param jsonLine
	 * 			the json string containing the info about the List<MsgDetails> to be created.
	 * @return the List<MsgDetails> object parsed from the jsonLine.
	 * 			
	 */
	private List<MsgDetails> parseJsonString(String jsonLine) {
		Type listType = new TypeToken<List<MsgDetails>>() {
		}.getType();
		Gson gson = new Gson();
		String json = "[" + jsonLine + "]"; // gson.toJson(target, listType);
		List<MsgDetails> target2 = gson.fromJson(json, listType);
		return target2;
	}
}
