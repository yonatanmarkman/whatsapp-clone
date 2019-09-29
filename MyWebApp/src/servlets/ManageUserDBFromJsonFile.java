package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import servletClasses.Channel;
import servletClasses.Message;
import servletClasses.User;
import servletClasses.UserChannel;

/**
 * A listener that reads the database from the computer and populates the data
 * into a Derby database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 * 
 **/
@WebListener
public class ManageUserDBFromJsonFile implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ManageUserDBFromJsonFile() {
	}

	/**
	 * Method to check if the table already exists.
	 * 
	 * @param e
	 * 			error object to check
	 * @returns true if table exists, false if not.
	 */
	private boolean tableAlreadyExists(SQLException e) {
		boolean exists;
		if (e.getSQLState().equals("X0Y32")) {
			exists = true;
		} else {
			exists = false;
		}
		return exists;
	}

	/**
	 * This method initializes the user database.
	 * 
	 * @param event
	 * 			event that is triggered when the website is run.
	 * 
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext cntx = event.getServletContext();
		try {
			// obtain db data source from Tomcat's context
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			boolean created = false;
			try {
				// create Customers table
				Statement stmt = conn.createStatement();
				createTable("users", stmt);
				createTable("channels", stmt);
				createTable("messages", stmt);
				createTable("userChannels", stmt);
				// commit update
				conn.commit();
				stmt.close();
			} catch (SQLException e) {
				// check if exception thrown since table was already created (so
				// we created the database already
				// in the past
				created = tableAlreadyExists(e);
				if (!created) {
					throw e;// re-throw the exception so it will be caught in
							// the
					// external try..catch and recorded as error in the log
				}
			}

			// if no database exist in the past - further populate its records
			// in the table
			if (!created) {
				// populate customers table with customer data from json file
				PreparedStatement pstmt = null;// conn.prepareStatement(AppConstants.INSERT_USER_STMT);
				populateUsers(pstmt, conn);
				populateChannels(pstmt, conn);
				populateMessages(pstmt, conn);
				populateUserChannels(pstmt, conn);
				// commit update
				// close statements
			}
			// close connection
			conn.close();
		} catch (SQLException | NamingException e) {
			// log error
			cntx.log("Error during database initialization", e);
		}
	}

	/**
	 * Method to destroy the database copy at server shutdown.
	 * 
	 * @param event
	 * 			the event of the server closing.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext cntx = event.getServletContext();
		// shut down database
		try {
			// obtain db data source from Tomcat's context and shutdown
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.SHUTDOWN);
			ds.getConnection();
			ds = null;
		} catch (SQLException | NamingException e) {
			cntx.log("Error shutting down database", e);
		}
	}

	/**
	 * Loads the users database from the json file, that is read from the input
	 * stream into a collection of User objects
	 * 
	 * @param is
	 *            input stream to json file
	 * @return collection of users
	 * @throws IOException
	 */
	private Collection<User> loadUsers(InputStream is) throws IOException {
		// wrap input stream with a buffered reader to allow reading the file
		// line by line
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder jsonFileContent = new StringBuilder();
		// read line by line from file
		String nextLine = null;
		while ((nextLine = br.readLine()) != null) {
			jsonFileContent.append(nextLine);
		}
		Gson gson = new Gson();
		// this is a require type definition by the Gson utility so Gson will
		// understand what kind of object representation should the json file
		// match
		Type type = new TypeToken<Collection<User>>() {
		}.getType();
		Collection<User> users = gson.fromJson(jsonFileContent.toString(), type);
		// close
		br.close();
		return users;
	}
	
	/**
	 * This method initializes the user database.
	 * 
	 * @param tableName
	 * 			the table name to load.
	 * 		  stmt
	 * 			the to execute.
	 * @throws SQLException 
	 */
	public void createTable(String tableName, Statement stmt) throws SQLException {
		String command = "";
		boolean created = false;
		switch (tableName) {
		case "users":
			command = AppConstants.CREATE_USERS_TABLE;
			break;
		case "userChannels":
			command = AppConstants.CREATE_USER_CHANNELS_TABLE;
			break;
		case "channels":
			command = AppConstants.CREATE_CHANNELS_TABLE;
			break;
		case "messages":
			command = AppConstants.CREATE_MESSAGES_TABLE;
			break;
		}
		try {
			stmt.executeUpdate(command);
		} // handling the exceptions
		catch (SQLException exception) {
			// getting the error code
			int errorCode = exception.getErrorCode();
			// if the error code means the table already exists
			// that's OK - otherwise - throw an exception
			if (errorCode != 30000)
				throw exception;
		}
	}

	/**
	 * This method initializes the user database.
	 * 
	 * @param pstmt
	 * 			prepared statement to execute.
	 * 		  conn
	 * 			connection the database
	 * @throws SQLException 
	 */
	public void populateUsers(PreparedStatement pstmt, Connection conn) throws SQLException {
		pstmt = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
		Collection<User> users = new ArrayList<User>();
		for (User user : users) {
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getNickname());
			pstmt.setString(4, user.getDescription());
			pstmt.setString(5, user.getImage());
			pstmt.executeUpdate();
		}
		conn.commit();
		pstmt.close();

	}
	
	/**
	 * This method initializes the channels database.
	 * 
	 * @param pstmt
	 * 			prepared statement to execute.
	 * 		  conn
	 * 			connection the database
	 * @throws SQLException 
	 */
	public void populateChannels(PreparedStatement pstmt, Connection conn) throws SQLException {
		pstmt = conn.prepareStatement(AppConstants.INSERT_CHANNEL_STMT);
		Collection<Channel> channels = new ArrayList<Channel>();
		for (Channel channel : channels) {
			// pstmt.setInt(1, channel.getChannelId());
			pstmt.setString(1, channel.getChannelName());
			pstmt.setString(2, channel.getChannelDescription());
			pstmt.setString(3, channel.getType());
			pstmt.executeUpdate();
		}
		conn.commit();
		pstmt.close();

	}

	/**
	 * This method initializes the messages database.
	 * 
	 * @param pstmt
	 * 			prepared statement to execute.
	 * 		  conn
	 * 			connection the database
	 * @throws SQLException 
	 */
	public void populateMessages(PreparedStatement pstmt, Connection conn) throws SQLException {
		pstmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT);
		Collection<Message> messages = new ArrayList<Message>();
		for (Message message : messages) {
			Calendar sentCal = Calendar.getInstance();
			sentCal = message.getSentTime();

			// pstmt.setInt(1, message.getMessageId());
			pstmt.setInt(1, message.getChannelId());
			pstmt.setString(2, message.getUserName());
			pstmt.setString(3, message.getNickName());
			pstmt.setString(4, message.getPhoto());
			pstmt.setTimestamp(5, new Timestamp(sentCal.getTimeInMillis()));
			pstmt.setString(6, message.getContent());
			pstmt.setInt(7, message.getParentMsgId());
			pstmt.executeUpdate();
		}
		conn.commit();
		pstmt.close();

	}


	/**
	 * This method initializes the subscriptions database.
	 * 
	 * @param pstmt
	 * 			prepared statement to execute.
	 * 		  conn
	 * 			connection the database
	 * @throws SQLException 
	 */
	public void populateUserChannels(PreparedStatement pstmt, Connection conn) throws SQLException {
		pstmt = conn.prepareStatement(AppConstants.INSERT_USERCHANNEL_STMT);
		Collection<UserChannel> userChannels = new ArrayList<UserChannel>();
		for (UserChannel userChannel : userChannels) {

			Calendar enterCal = Calendar.getInstance();
			enterCal = userChannel.getEnterTime();
			Calendar exitCal = Calendar.getInstance();
			exitCal = userChannel.getExitTime();
			Calendar subCal = Calendar.getInstance();
			subCal = userChannel.getSubsciptionTime();

			pstmt.setInt(1, userChannel.getChannelId());
			pstmt.setString(2, userChannel.getUsername());
			pstmt.setTimestamp(3, new Timestamp(enterCal.getTimeInMillis()));
			pstmt.setTimestamp(4, new Timestamp(exitCal.getTimeInMillis()));
			pstmt.setTimestamp(5, new Timestamp(subCal.getTimeInMillis()));
			pstmt.executeUpdate();
		}
		conn.commit();
		pstmt.close();

	}

}