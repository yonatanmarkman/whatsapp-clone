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

import servletClasses.SubDetails;


/**
 * This servlet is in charge on updating the exit
 * time of specific user to specific channel
 * 
 * @author Sagi
 *
 */
@WebServlet(description = "", urlPatterns = { "/UpdateExitTime" })
public class UpdateExitTimeServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	public UpdateExitTimeServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String js = getRequestData(request, response);
		updateExitTime(request, response, js);
		String responseToSend = (" { \"status\": \"" + 0 + "\" } ");
		super.sendData(response, responseToSend);
	}

	
	
	/**
	 * In this help method we try
	 * to connect to the data base and 
	 * updsate the enry date according to he request.
	 * @param request
	 * @param response
	 * @param js
	 * @return true
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean updateExitTime(HttpServletRequest request, HttpServletResponse response, String js)
			throws ServletException, IOException {
		SubDetails subDetails = parseJsonString(js).get(0);
		int channelId = subDetails.getChannelId();
		String username = subDetails.getUsername();
		// getting current time
		Calendar currentTime = Calendar.getInstance();

		try {
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			// insert new userchannel record.
			try {
				java.util.Date utilDate = new java.util.Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(utilDate);
				cal.set(Calendar.MILLISECOND, 0);
				
				//Timestamp t= new Timestamp(currentTime);
				stmt = conn.prepareStatement(AppConstants.UPDATE_USERCHANNEL_EXIT_TIME_STMT);
				stmt.setTimestamp(1,new java.sql.Timestamp(cal.getTimeInMillis()) );
				stmt.setInt(2, channelId);
				stmt.setString(3, username);

				stmt.executeUpdate();
				stmt.close();
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
		List<SubDetails> target2 = gson.fromJson(json, listType);
		return target2;
	}
}
