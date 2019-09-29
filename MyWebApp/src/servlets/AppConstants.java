package servlets;

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.reflect.TypeToken;

import servletClasses.*;


/**
 * This class holds all the string constants used to access, view and manipulate the SQL database.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public interface AppConstants {
	//basic database constants.
	public final String USERNAME = "username";
	public final Type USER_COLLECTION = new TypeToken<Collection<User>>() {
	}.getType();
	public final Type CHANNEL_COLLECTION = new TypeToken<Collection<Channel>>() {
	}.getType();
	public final Type USERCHANNEL_COLLECTION = new TypeToken<Collection<UserChannel>>() {
	}.getType();
	public final Type MESSAGE_COLLECTION = new TypeToken<Collection<Message>>() {
	}.getType();
	public final Type MESSAGE_TYPE = new TypeToken<Message>() {
	}.getType();

	// derby constants
	public final String DB_NAME = "DB_NAME";
	public final String DB_DATASOURCE = "DB_DATASOURCE";
	public final String PROTOCOL = "jdbc:derby:";
	public final String OPEN = "Open";
	public final String SHUTDOWN = "Shutdown";
	public final String DATABASE_CONNCTION_STRING = "jdbc:derby:projectDB";
	public static final String DERBY_EMBEDDED_DRIVER_PATH = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String RESPONSE_WRITER_ERROR = "Error getting the response writer";
	public final String DATABASE_CREATE = DATABASE_CONNCTION_STRING + ";create=true";

	// SQL Strings for managing the users table
	public final String CREATE_USERS_TABLE = "CREATE TABLE Users (" + "Username VARCHAR(10) NOT NULL,"
			+ "Password VARCHAR(8) NOT NULL," + "Nickname VARCHAR(20) NOT NULL," + "Description VARCHAR(50),"
			+ "URL VARCHAR(5000)," + "PRIMARY KEY (Username)," + "UNIQUE (Nickname)" + ")";
	public final String DELETE_USERS_TABLE = "DROP TABLE USERS";

	public final String INSERT_USER_STMT = "INSERT INTO USERS VALUES(?,?,?,?,?)";
	public final String SELECT_ALL_USERS_STMT = "SELECT * FROM USERS";
	public final String SELECT_USER_LOGIN_STMT = "SELECT * FROM USERS " + "WHERE Username=? AND Password=?"; 
	public final String SELECT_USER_STMT = "SELECT * FROM USERS " + "WHERE Username=? OR Nickname=?"; 

	// SQL Strings for managing the channels table
	public final String CREATE_CHANNELS_TABLE = "CREATE TABLE Channels ("
			+ "ChannelID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "ChannelName VARCHAR(30) NOT NULL,"
			+ "ChannelDescription VARCHAR(500),"
			+ "Type VARCHAR(7) NOT NULL,"
			+ "PRIMARY KEY (ChannelID)" + ")";
	public final String INSERT_CHANNEL_STMT = "INSERT INTO CHANNELS(CHANNELNAME,CHANNELDESCRIPTION,TYPE) VALUES(?,?,?)";
	public final String SELECT_ALL_CHANNELS_STMT = "SELECT * FROM CHANNELS";
	public final String SELECT_CHANNELS_BY_TYPE_STMT = "SELECT * FROM CHANNELS WHERE Type=?";
	public final String SELECT_PUBLIC_CHANNELS_AND_ID = "SELECT * FROM CHANNELS WHERE Type='public' AND ChannelId=?";
	public final String SELECT_PUBLIC_CHANNELS_AND_ID_MULTIPLE = "SELECT * FROM CHANNELS WHERE Type='public' AND (ChannelID!=?";
	public final String SELECT_CHANNEL_BY_ID_STMT = "SELECT * FROM CHANNELS " + "WHERE ChannelID=?";
	
	public final String SELECT_CHANNEL_BY_NAME_STMT = "SELECT * FROM CHANNELS " + "WHERE ChannelName=?";
	
	public final String SELECT_USER_PUBLIC_CHANNELS = "SELECT * FROM USERCHANNELS INNER JOIN CHANNELS ON "
			+ " USERCHANNELS.channelId = CHANNELS.channelId " + "WHERE CHANNELS.Type = 'public' AND"
			+ " USERCHANNELS.userName = ? ";
	public final String SELECT_USER_PRIVATE_CHANNELS = "SELECT * FROM USERCHANNELS INNER JOIN CHANNELS ON "
			+ " USERCHANNELS.channelId = CHANNELS.channelId " + "WHERE CHANNELS.Type = 'private' AND"
			+ " USERCHANNELS.userName = ? ";
	
	public final String AND_CONDITION_BY_CHANNELID = " AND ChannelID!=?";

	public final String privateChannel = "private";
	public final String publicChannel = "public";

	// SQL Strings for managing the messages table
	public final String CREATE_MESSAGES_TABLE = "CREATE TABLE Messages ("
			+ "MessageID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "ChannelID INTEGER NOT NULL," + "UserName VARCHAR(10) NOT NULL," + "Nickname VARCHAR(20) NOT NULL,"
			+ "Photo VARCHAR(5000) NOT NULL," + "SentTime TIMESTAMP NOT NULL," + "Content VARCHAR(500) NOT NULL,"
			+ "ParentMsgID INTEGER NOT NULL," + "PRIMARY KEY (MessageID)," + "FOREIGN KEY (ChannelID) REFERENCES Channels,"
			+ "FOREIGN KEY (UserName) REFERENCES Users"// ,"
			+ ")";

	public final String SELECT_ALL_MESSAGES_STMT = "SELECT * FROM MESSAGES";

	public final String SELECT_MESSAGES_BY_ID_STMT = "SELECT * FROM MESSAGES WHERE ChannelID=?";

	public final String SELECT_MESSAGES_BY_ID_AND_SUBSCRIPTION_STMT = "SELECT * FROM MESSAGES WHERE ChannelID=? "
			+ "AND SentTime >= ?";
	
	public final String JOIN_USERCHANNELS_AND_CHANNELS = "SELECT * FROM MESSAGES WHERE ChannelID=? "
			+ "AND SentTime <= ? AND UserName != ?";
	
	public final String JOIN_USERCHANNELS_AND_CHANNELS_TWO ="select * from UserChannels"
			+ " inner join Messages on UserChannels.channelId =  Messages.channelId"
			+ " where Messages.ChannelID= ? AND "
			+ "Messages.SentTime > ? AND UserChannels.UserName != ?";
	
	public final String SELECT_LAST_MESSAGE_STMT = "SELECT TOP 1 * FROM MESSAGES ORDER BY SentTime DESC";
	public final String INSERT_MESSAGE_STMT = "INSERT INTO MESSAGES(CHANNELID,USERNAME,NICKNAME,PHOTO,SENTTIME,CONTENT,PARENTMSGID) VALUES(?,?,?,?,?,?,?)";
	
	// SQL Strings for managing the subscriptions table
	public final String CREATE_USER_CHANNELS_TABLE = "CREATE TABLE UserChannels (" 
			+ "ChannelID INTEGER NOT NULL,"
			+ "Username VARCHAR(10) NOT NULL,"
			+ "EnterTime TIMESTAMP,"
			+ "ExitTime TIMESTAMP,"
			+ "SubscriptionTime TIMESTAMP,"
			+ "PRIMARY KEY (ChannelID,Username),"
			+ "FOREIGN KEY (ChannelID) REFERENCES Channels,"
			+ "FOREIGN KEY (Username) REFERENCES Users" + ")";
	public final String SELECT_ALL_USER_CHANNEL_STMT = "SELECT * FROM UserChannels";
	public final String SELECT_USERCHANNEL_BY_ID_STMT = "SELECT * FROM UserChannels WHERE ChannelID=?";
	public final String SELECT_USERCHANNEL_BY_ID_AND_USERNAME_STMT = "SELECT * FROM UserChannels WHERE ChannelID=? AND Username=?";
	public final String SELECT_USERCHANNEL_BY_USERNAME_STMT = "SELECT * FROM UserChannels WHERE Username=?";
	public final String INSERT_USERCHANNEL_STMT = "INSERT INTO UserChannels VALUES(?,?,?,?,?)";
	public final String DELETE_USERCHANNEL_STMT = "DELETE FROM UserChannels WHERE ChannelID=? AND Username=?";

	public final String UPDATE_USERCHANNEL_ENTRY_TIME_STMT = "UPDATE UserChannels " + "SET EnterTime=? "
			+ "WHERE ChannelID=? AND Username=?";
	public final String UPDATE_USERCHANNEL_EXIT_TIME_STMT = "UPDATE UserChannels " + "SET ExitTime=? "
			+ "WHERE ChannelID=? AND Username=?";
}
