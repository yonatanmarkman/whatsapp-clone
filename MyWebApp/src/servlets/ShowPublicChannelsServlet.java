package servlets;

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
import servletClasses.UserChannel;

import com.google.gson.Gson;

import servletClasses.Channel;
import servletClasses.UserChannel;
import servlets.AppConstants;

/**
 * This Servlet is in charge on displaying all the public channel of the user
 * 
 * @author Sagi
 *
 */
@WebServlet(description = "", urlPatterns = { "/publicChannels","/publicChannels/*","/publicChannels/userName/*" })
public class ShowPublicChannelsServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	public ShowPublicChannelsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();

			Collection<Channel> channelsResult = new ArrayList<Channel>();

			Statement stmt;
			// lets try to connect the database and get the information about the public channels of the user.
			int counter = 0;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_CHANNELS_STMT);
				while (rs.next() && counter < 100) { // the maximum channels that can be display is 100 according to the requierments
					channelsResult.add(new Channel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
					counter++;
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
