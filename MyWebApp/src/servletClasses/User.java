package servletClasses;

/**
 * 
 * The User object stores the information associated with a user.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

public class User {
	private String username; // the username field.
	private String password; // the password field.
	private String nickname; // the nickname field.
	private String description; // the description field.
	private String image; // the url address of the image of the user.
	
	/* basic constructor */

	public User(String username, String password, String nickname, String description, String image) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.description = description;
		this.image = image;
	}
	
	/* basic getters */

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public String toString() {
		return "toString() : [ uname: " + username + ", pwd: " + password + " , nick: " + nickname + " , desc: "
				+ description + " , img: " + image + " ]";
	}
}
