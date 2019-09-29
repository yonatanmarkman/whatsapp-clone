package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import servletClasses.Channel;

/**
 * This servlet is used to search public channels by name.
 * 
 * @author YONATAN
 * @since 01-03-2017
 * 
 **/
@WebServlet(description = "", urlPatterns = { "/SearchPublicChannels", "/SearchPublicChannels/*",
		"/SearchPublicChannels/channelName/*" })
public class SearchPublicChannelServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SearchPublicChannelServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * doGet method to get search query from client. 
	 *
	 * @param request
	 * 			response
	 * @throws ServletException, IOException 
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("dogettt");
		Collection<Channel> searchResult = checkIfChannelExists(request, response);
		searchResult = filterPrivateChannels((ArrayList<Channel>) searchResult);
		System.out.println("filtered results: " + searchResult);
		String channelsList = "[]";

		if (searchResult == null || searchResult.size() == 0) {

		} else {
			Gson gson = new Gson();
			// convert from customers collection to json
			channelsList = gson.toJson(searchResult, AppConstants.CHANNEL_COLLECTION);
		}
		// getting the object for text output in the packet
		// sending the status
		System.out.println("channels list results: " + channelsList);
		String responseToSend = channelsList;
		super.sendData(response, responseToSend);
	}

	/**
	 * Method to check if the query of the user leads to an existing channel.
	 *
	 * @param request
	 * 			response
	 * @throws ServletException, IOException 
	 */
	protected Collection<Channel> checkIfChannelExists(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("searching for channels....");
		// accessing channel database by name;
		String uri = request.getRequestURI();

		String c = "channelName";
		String channelName = uri.substring(uri.indexOf(c) + c.length() + 1);
		channelName = channelName.replaceAll("\\%20", " ");
		System.out.println("input name: " + channelName);
		// loading database stuff..
		Collection<Channel> searchResults = new ArrayList<Channel>();

		int counter = 0;
		
		try {
			// obtain projectDB data source from Tomcat's context
			Connection conn = getDatabase(request, response);
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_CHANNEL_BY_NAME_STMT);
				stmt.setString(1, channelName);
				ResultSet rs = stmt.executeQuery();
				while (rs.next() && counter < 100) {
					searchResults.add(new Channel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
					System.out.println("counter is: "+counter);
					counter++;
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while searching for channels", e);
				response.sendError(500);// internal server error
			}
			conn.close();
			System.out.println("channel search results:" + searchResults.toString());
		} catch (SQLException e) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);// internal server error
		}

		if (searchResults.size() != 0)
			return searchResults;
		else
			return null;
	}

	
	/**
	 * Method to filter out private channels from the user's search.
	 *
	 * @param searchResult
	 * 			search results of the query
	 */
	private ArrayList<Channel> filterPrivateChannels(ArrayList<Channel> searchResult) {
		ArrayList<Channel> searchResultNew = new ArrayList<Channel>();
		if (searchResult != null && searchResult.size() > 0) {
			for (Channel ch : searchResult) {
				if (ch.getType().equals(AppConstants.publicChannel)) {
					searchResultNew.add((ch));
				}
			}
			return searchResultNew;
		}
		return null;
	}

	/**
	 * Basic doPost method.
	 *
	 * @param request
	 * 			response
	 * @throws ServletException, IOException 
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
