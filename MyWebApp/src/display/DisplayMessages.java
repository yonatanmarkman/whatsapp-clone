package display;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
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

import servletClasses.Channel;
import servletClasses.Message;
import servlets.AppConstants;

/**
 * This class is used via the browser to display the whole message database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 */
@WebServlet(description = "Servlet to provide details about messages", urlPatterns = { "/messages" })
public class DisplayMessages extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayMessages() {
		super();
	}
	
	/**
	 * doGet method to print all the messages table.
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

			Collection<Message> messagesResult = new ArrayList<Message>();
			Calendar sentCal = Calendar.getInstance();
			Statement stmt;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_MESSAGES_STMT);
				while (rs.next()) {
					sentCal.setTimeInMillis(rs.getTimestamp(6).getTime());
					messagesResult.add(new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
							rs.getString(5), sentCal, rs.getString(7), rs.getInt(8)));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for messages", e);
				response.sendError(500);
			}

			conn.close();
			Gson gson = new Gson();

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
	 * 			
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
