package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;


/**
 * This is the main ChatmeServlet. All the servlets we use inherit this class,
 * which contains useful methods for getting data from JSON objects, as well
 * as getting the HttpRequest printer and the URI data in the http get requests.
 * 
 * @author YONATAN
 * @since 01-03-2017
 **/

@WebServlet(description = "", urlPatterns = { "/MainServlet" })
public class ChatmeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor.
	 */
	public ChatmeServlet() {
		super();
	}

	/**
	 * Basic doGet method.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException	
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * Basic doGet method.
	 *
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException	
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Method to get print writer form HttpServletResponse response
	 *
	 * @param response
	 * 			the response to contact the client side
	 * @return the printwriter from the response.
	 */
	protected static PrintWriter getPrintWriter(HttpServletResponse response) {
		// defining the writer
		PrintWriter writer = null;
		try {
			// getting the writer
			writer = response.getWriter();
		} // handling the exception
		catch (IOException exception) {
			System.err.println(AppConstants.RESPONSE_WRITER_ERROR);
			exception.printStackTrace();
		}
		// returning the writer
		return writer;
	}

	/**
	 * Method to send data to the printwriter returned from the response.
	 *
	 * @param response
	 * 			the response to contact the client side
	 * 		  message
	 * 			the message to send to the client
	 *
	 */
	protected static void sendData(HttpServletResponse response, String message) {
		// getting the object for text output in the packet
		PrintWriter writer = getPrintWriter(response);
		// sending the data
		writer.println(message);
		writer.close();
	}

	
	/**
	 * Method to get the connection to the whole database.
	 *
	 * @param HttpServletRequest request
	 * 			the request from the client side
	 * 		  HttpServletRequest request
	 * 			the response to contact the client side
	 * @return the connection to the derby database.
	 * @throws IOException	
	 */
	protected Connection getDatabase(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource) context
					.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			return conn;
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while accessing DB", e);
			response.sendError(500);// internal server error
		}
		return null;
	}

	
	/**
	 * Method to get the json request data from the client
	 *
	 * @param HttpServletRequest request
	 * 			the request from the client side
	 * 		  HttpServletRequest request
	 * 			the response to contact the client side
	 * @return the string representing the request data.
	 * @throws ServletException, IOException
	 */
	protected String getRequestData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
		} catch (Exception e) {
			/* report an error */
		}
		return jb.toString();
	}

	/**
	 * Method to get request data from url, when passing a parameter to the servlet url
	 * when using a http get request.
	 *
	 * @param HttpServletRequest request
	 * 			the request from the client side
	 * 		  HttpServletRequest request
	 * 			the response to contact the client side
	 * 		  String field
	 * 			the field from which to exctract the data.
	 * @return the data exctracted from the field.
	 * @throws ServletException, IOException
	 */
	protected String getRequestDataFromUrl(HttpServletRequest request, HttpServletResponse response, String field)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String fieldValue = uri.substring(uri.indexOf(field) + field.length() + 1);
		fieldValue = fieldValue.replaceAll("\\%20", " ");
		return fieldValue;
	}
}
