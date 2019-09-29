package display;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import servletClasses.User;
import servlets.AppConstants;

/**
 * This class is used via the browser to display the whole user database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 */
@WebServlet(description = "Servlet to provide details about users", urlPatterns = { "/users", "/users/username/*" })
public class DisplayUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayUsers() {
		super();
	}

	/**
	 * doGet method to print all the users table.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * 			
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();

			Collection<User> usersResult = new ArrayList<User>();

			Statement stmt;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_USERS_STMT);
				while (rs.next()) {
					usersResult.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5)));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for users", e);
				response.sendError(500);
			}

			conn.close();
			Gson gson = new Gson();
			String userJsonResult = gson.toJson(usersResult, AppConstants.USER_COLLECTION);
			PrintWriter writer = response.getWriter();
			writer.println(userJsonResult);
			writer.close();
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);
		}
	}

	/**
	 * Basic doPost method.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * 			
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
