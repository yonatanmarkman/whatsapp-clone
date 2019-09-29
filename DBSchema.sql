DDL------------------------------------------------

/*SQL query for create the Users table*/
 CREATE TABLE Users (
	Username VARCHAR(10) NOT NULL,
	Password VARCHAR(8) NOT NULL, 
	Nickname VARCHAR(20) NOT NULL,  
	Description VARCHAR(50),
	URL VARCHAR(5000), 
	PRIMARY KEY (Username),
	UNIQUE (Nickname));

	
/* SQL query for create the Channels table	*/	
CREATE TABLE Channels (
	ChannelID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	ChannelName VARCHAR(30) NOT NULL,
	ChannelDescription VARCHAR(500),
	Type VARCHAR(7) NOT NULL,
	PRIMARY KEY (ChannelID));
	
	
/* SQL query for create the Messages table	*/
CREATE TABLE Messages (
	MessageID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	ChannelID INTEGER NOT NULL,
	UserName VARCHAR(10) NOT NULL,
	Nickname VARCHAR(20) NOT NULL,
	Photo VARCHAR(5000) NOT NULL,
	SentTime TIMESTAMP NOT NULL,
	Content VARCHAR(500) NOT NULL,
	ParentMsgID INTEGER NOT NULL,
	PRIMARY KEY (MessageID),
	FOREIGN KEY (ChannelID) REFERENCES Channels,
	FOREIGN KEY (UserName) REFERENCES Users);
	
/* SQL query for create the UserChannels table. This table contains all the users in channels */
CREATE TABLE UserChannels (
			ChannelID INTEGER NOT NULL,
			Username VARCHAR(10) NOT NULL,
			EnterTime TIMESTAMP,
			ExitTime TIMESTAMP,
			SubscriptionTime TIMESTAMP,
			PRIMARY KEY (ChannelID,Username),
			FOREIGN KEY (ChannelID) REFERENCES Channels,
			FOREIGN KEY (Username) REFERENCES Users);
			
DML------------------------------------------------

---Inserts Queries ---
 
	/* insert new user to the users table */
	INSERT INTO USERS VALUES(?,?,?,?,?)
	
	/* insert new channel to the channels table */
	INSERT INTO CHANNELS(CHANNELNAME,CHANNELDESCRIPTION,TYPE) VALUES(?,?,?)
	
	/* insert new message to the messages table */
	INSERT INTO MESSAGES(CHANNELID,USERNAME,NICKNAME,PHOTO,SENTTIME,CONTENT,PARENTMSGID) VALUES(?,?,?,?,?,?,?)
	
	/* insert new record to the userChannels table which represents subscription */
	INSERT INTO UserChannels VALUES(?,?,?,?,?)
	
---Select Queries---	
	
	/* gets all users */
	SELECT * FROM USERS
	
	/* gets users by username and nickname */
	SELECT * FROM USERS WHERE Username=? OR Nickname=?
    
	/* gets users by username and password */
	SELECT * FROM USERS " + "WHERE Username=? AND Password=?
	
	/* gets channel by id channel */
	SELECT * FROM CHANNELS WHERE ChannelID=?
	
	/* gets channels by the channelName*/
	SELECT * FROM CHANNELS WHERE ChannelName=?
	
	/* gets all channels */
	SELECT * FROM CHANNELS
	
	/* gets channels by the type ( public or private) */
	SELECT * FROM CHANNELS WHERE Type=?
	
	/* gets all the public channels by id */
	SELECT * FROM CHANNELS WHERE Type='public' AND ChannelId=?
	
	/* gets all the public channels except one (which is recieved as input to the query) */
	SELECT * FROM CHANNELS WHERE Type='public' AND ChannelID!=?
	
	/* gets all the public channels of specific user */
	SELECT * FROM USERCHANNELS INNER JOIN CHANNELS ON USERCHANNELS.channelId = CHANNELS.channelId WHERE CHANNELS.Type = 'public' AND USERCHANNELS.userName = ? 
	
	/*  gets all the public channels of specific user  */
	SELECT * FROM USERCHANNELS INNER JOIN CHANNELS ON USERCHANNELS.channelId = CHANNELS.channelId WHERE CHANNELS.Type = 'private' AND USERCHANNELS.userName = ? 	
	

	/* gets all the messages in all channels*/
	SELECT * FROM MESSAGES
 
	/* gets all the messages in specific channel*/
	SELECT * FROM MESSAGES WHERE ChannelID=?
 
	/* gets all the messages in specific channel*/
	SELECT * FROM MESSAGES WHERE ChannelID=? 	AND SentTime >= ?
 
	/* gets all the messages that sent after the user left the channel*/
	SELECT * FROM MESSAGES WHERE ChannelID=? AND SentTime <= ? AND UserName != ?

	/* gets all the messages that sent after the user left the channel*/
	public final String JOIN_USERCHANNELS_AND_CHANNELS_TWO =SELECT * FROM UserChannels INNER JOIN Messages ON UserChannels.channelId =  Messages.channelId
	where Messages.ChannelID= ? AND 
	Messages.SentTime > ? AND UserChannels.UserName != ?
	
	/* gets the last sent message */
	SELECT TOP 1 * FROM MESSAGES ORDER BY SentTime DESC

	/* show all the records from 'userChannels' */
	SELECT * FROM UserChannels
	
	/* show specific channel*/
	SELECT * FROM UserChannels WHERE ChannelID=?
	
	/* show specific channel for specific user */
	SELECT * FROM UserChannels WHERE ChannelID=? AND Username=?
	
	/* show channels for specific user */
	SELECT * FROM UserChannels WHERE Username=?
	
	
	
---Update Queries--

	/* Update entry channel time of the user*/
	UPDATE UserChannels SET EnterTime=? WHERE ChannelID=? AND Username=?

	/* Update exit channel time of the user*/
	UPDATE UserChannels SET ExitTime=? WHERE ChannelID=? AND Username=?

 	
---Delete Queries--

	/* Delete channel for specific user */
	DELETE FROM UserChannels WHERE ChannelID=? AND Username=?
