package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Login;

public class LoginDao {
	/*
	 * This class handles all the database operations related to login functionality
	 */
	
	protected static final String dmConn = "jdbc:mysql://mysql3.cs.stonybrook.edu:3306/jalleonardi";
	protected static final String dmUser = "jalleonardi";
	protected static final String dmPass = "113332225";
	
	public Login login(String username, String password, String role) {
		/*
		 * Return a Login object with role as "manager", "customerRepresentative" or "customer" if successful login
		 * Else, return null
		 * The role depends on the type of the user, which has to be handled in the database
		 * username, which is the email address of the user, is given as method parameter
		 * password, which is the password of the user, is given as method parameter
		 * Query to verify the username and password and fetch the role of the user, must be implemented
		 */
		
		if (username != null && password != null && role != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(dmConn, dmUser, dmPass);
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Retrieve Login entry from DB
				query = connection.prepareStatement("SELECT Password FROM Login WHERE Username = ?");
				query.setString(1, username);
				results = query.executeQuery();
				if (!results.next() || !results.getString("Password").equals(password)) return null;
				Login login = new Login();
				login.setUsername(username);
				login.setPassword(password);
				login.setRole(role);
				
				results.close();
				query.close();
				connection.close();
				return login;
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		
		return null;
		
	}
	
	public String addUser(Login login) {
		/*
		 * Query to insert a new record for user login must be implemented
		 * login, which is the "Login" Class object containing username and password for the new user, is given as method parameter
		 * The username and password from login can get accessed using getter methods in the "Login" model
		 * e.g. getUsername() method will return the username encapsulated in login object
		 * Return "success" on successful insertion of a new user
		 * Return "failure" for an unsuccessful database operation
		 */
		
		if (login != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(dmConn, dmUser, dmPass);
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing user
				query = connection.prepareStatement("SELECT Username FROM Login WHERE Username = ?");
				query.setString(1, login.getUsername());
				results = query.executeQuery();
				if (results.next()) return "failure";
				
				// Add new user
				query = connection.prepareStatement("INSERT INTO Login VALUES (?, ?)");
				query.setString(1, login.getUsername());
				query.setString(2, login.getPassword());
				query.executeUpdate();
				
				connection.commit();
				results.close();
				query.close();
				connection.close();
				
				return "success";			
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		
		return "failure";
		
	}

}
