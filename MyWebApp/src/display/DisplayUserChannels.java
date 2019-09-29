package display;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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
import servletClasses.UserChannel;
import servlets.AppConstants;

/**
 * This class is used via the browser to display the whole subscription database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 */
@WebServlet(description = "", urlPatterns = { "/userChannels" })
public class DisplayUserChannels extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayUserChannels() {
		super();
	}

	/**
	 * doGet method to print all the subscriptions table.
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

			Collection<UserChannel> userChannelResult = new ArrayList<UserChannel>();
			Calendar enterCal = Calendar.getInstance();
			Calendar exitCal = Calendar.getInstance();
			Calendar subCal = Calendar.getInstance();
			Statement stmt;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_USER_CHANNEL_STMT);
				while (rs.next()) {
					enterCal.setTimeInMillis(rs.getTimestamp(3).getTime());
					exitCal.setTimeInMillis(rs.getTimestamp(4).getTime());
					subCal.setTimeInMillis(rs.getTimestamp(5).getTime());
					userChannelResult.add(new UserChannel(rs.getInt(1), rs.getString(2), enterCal, exitCal, subCal));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for userChannels", e);
				response.sendError(500);// internal server error
			}

			conn.close();
			Gson gson = new Gson();
			String userChannelJsonResult = gson.toJson(userChannelResult, AppConstants.USERCHANNEL_COLLECTION);
			PrintWriter writer = response.getWriter();
			writer.println(userChannelJsonResult);
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