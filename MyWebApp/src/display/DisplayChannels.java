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

import servletClasses.Channel;
import servlets.AppConstants;

/**
 * This class is used via the browser to display the whole channel database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 */
@WebServlet(description = "Servlet to provide details about channels", urlPatterns = { "/channels" })
public class DisplayChannels extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayChannels() {
		super();
	}
	
	/**
	 * doGet method to print all the channels table.
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

			Collection<Channel> channelsResult = new ArrayList<Channel>();

			Statement stmt;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_CHANNELS_STMT);
				while (rs.next()) {
					channelsResult.add(new Channel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for channels", e);
				response.sendError(500);
			}

			conn.close();
			Gson gson = new Gson();
			String channelJsonResult = gson.toJson(channelsResult, AppConstants.CHANNEL_COLLECTION);
			PrintWriter writer = response.getWriter();
			writer.println(channelJsonResult);
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
