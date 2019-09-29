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

import servletClasses.User;

/**
 * This Servlet handles the ligin requests
 * 
 * @author Sagi
 *
 */
@WebServlet(description = "Servlet to provide details about sign up", urlPatterns = { "/SignUp" })
public class SignUpServlet extends LogInServlet {
	private static final long serialVersionUID = 1L;

	public SignUpServlet() {
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

		int status = 0;
		User signUpUser = (User) parseJsonString(jb).get(0);
		String nickname = signUpUser.getNickname();
		String imageURL = signUpUser.getImage();

		User userResult = checkIfUserExists(request, response, jb); // userResult is null if the user doesn't exists..
		if (userResult == null) { // user doesn't exists - success
			status = 1;
			this.createNewUserInUserDB(request, response, jb);
		} else {
			if (nickname.equals(userResult.getNickname())) {
				status = 2;
			} else {
				status = 0;
			}
		}

		String responseToSend = (" { \"status\": \"" + status + "\", \"nickname\": \"" + nickname
				+ "\", \"imageURL\": \"" + imageURL + "\" } ");
		super.sendData(response, responseToSend);
	}
	/**
	 * This is helper method that inserts the new user to the DB.
	 * 
	 * @param request
	 * @param response
	 * @param jsonLine
	 * @throws ServletException
	 * @throws IOException
	 */
	private void createNewUserInUserDB(HttpServletRequest request, HttpServletResponse response, String jsonLine)
			throws ServletException, IOException {

		// accessing database;
		User signUpUser = parseJsonString(jsonLine).get(0);
		String username = signUpUser.getUsername();
		String password = signUpUser.getPassword();
		String nickname = signUpUser.getNickname();
		String description = signUpUser.getDescription();
		String URL = signUpUser.getImage();


		// loading database stuff..
		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			// try to connect the data base and exevute the insert query
			try {
				stmt = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
				stmt.setString(1, username);
				stmt.setString(2, password);
				stmt.setString(3, nickname);
				stmt.setString(4, description);
				stmt.setString(5, URL);
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
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param jsonLine
	 * @return User object that matches the request. (returns null id doesn't exists)
	 * @throws ServletException
	 * @throws IOException
	 */
	private User checkIfUserExists(HttpServletRequest request, HttpServletResponse response, String jsonLine)
			throws ServletException, IOException {
		// accessing database;

		User loggedInUser = parseJsonString(jsonLine).get(0);
		String username = loggedInUser.getUsername();
		String nickname = loggedInUser.getNickname();

		// loading database stuff..
		ArrayList<User> usersResult = new ArrayList<User>();
		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_USER_STMT);
				stmt.setString(1, username);
				stmt.setString(2, nickname);
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
	 * Method to parse json string to a List<User> object
	 *
	 * @param jsonLine
	 * 			the json string containing the info about the List<User> to be created.
	 * @return the List<User> object parsed from the jsonLine.
	 * 			
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
