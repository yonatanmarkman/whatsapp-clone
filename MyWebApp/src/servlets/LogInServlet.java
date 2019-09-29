package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.*;

/**
 * This servlet is used to check the login details of a client.
 * 
 * @author YONATAN
 * @since 01-03-2017
 * 
 **/
@WebServlet(description = "Servlet to provide details about sign up", urlPatterns = { "/LogIn" })
public class LogInServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public LogInServlet() {
		super();
	}
	
	/**
	 * Basic doGet method.
	 * 
	 * @param request
	 * 			the request from the client side.
	 * 		  response
	 * 			the response to contact the client side.
	 * @throws ServletException, IOException	
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
	
	/**
	 * Basic doPost method.
	 * 
	 * @param request
	 * 			the request from the client side.
	 * 		  response
	 * 			the response to contact the client side.
	 * @throws ServletException, IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//getting the data from the json string
		String js = getRequestData(request, response);
		int status = 0;
		String nickname = "";
		String imageURL = "";
		User userResult = checkIfUserExists(request, response, js);
		if (userResult == null) { //if user doesn't exist
			status = 1;
		} else { //if it exists - return it's credentials to present in the page.
			nickname = userResult.getNickname();
			imageURL = userResult.getImage();
		}
		// getting the object for text output in the packet
		// sending the status
		String responseToSend = " { \"status\": \"" + status + "\", \"nickname\": \"" + nickname
				+ "\", \"imageURL\": \"" + imageURL + "\" } ";
		super.sendData(response, responseToSend);
	}

	/**
	 * Basic doPost method.
	 * 
	 * @param request
	 * 			the request from the client side.
	 * 		  response
	 * 			the response to contact the client side.
	 * @returns true is user exists, false if not.
	 * @throws ServletException, IOException
	 */
	private User checkIfUserExists(HttpServletRequest request, HttpServletResponse response, String jsonLine)
			throws ServletException, IOException {
		// accessing database;

		User signUpUser = parseJsonString(jsonLine).get(0);
		String username = signUpUser.getUsername();
		String password = signUpUser.getPassword();
		// loading database stuff..
		ArrayList<User> usersResult = new ArrayList<User>();
		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_USER_LOGIN_STMT);
				stmt.setString(1, username);
				stmt.setString(2, password);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					usersResult.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5)));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for users", e);
				response.sendError(500);// internal server error
			}
			conn.close();
		} catch (SQLException e) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);// internal server error
		}

		if (usersResult.size() != 0)
			return usersResult.get(0); // user found
		else
			return null; // user not found
	}
	
	/**
	 * Method to parse json string to user list.
	 * 
	 * @param jsonLine
	 * 			the json string containing the details.
	 * @returns the list of the users parsed from the json.
	 */
	private List<User> parseJsonString(String jsonLine) {
		Type listType = new TypeToken<List<User>>() {
		}.getType();
		Gson gson = new Gson();
		String json = "[" + jsonLine + "]"; // gson.toJson(target, listType);
		List<User> target2 = gson.fromJson(json, listType);
		return target2;
	}
}
