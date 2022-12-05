package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Customer;
import model.Location;

public class CustomerDao {
	/*
	 * This class handles all the database operations related to the customer table
	 */

    public Customer getDummyCustomer() {
        Location location = new Location();
        location.setZipCode(11790);
        location.setCity("Stony Brook");
        location.setState("NY");

        Customer customer = new Customer();
        customer.setId("111-11-1111");
        customer.setAddress("123 Success Street");
        customer.setLastName("Lu");
        customer.setFirstName("Shiyong");
        customer.setEmail("shiyong@cs.sunysb.edu");
        customer.setLocation(location);
        customer.setTelephone("5166328959");
        customer.setCreditCard("1234567812345678");
        customer.setRating(1);

        return customer;
    }
    public List<Customer> getDummyCustomerList() {
        /*Sample data begins*/
        List<Customer> customers = new ArrayList<Customer>();

        for (int i = 0; i < 10; i++) {
            customers.add(getDummyCustomer());
        }
		/*Sample data ends*/

        return customers;
    }

    /**
	 * @param String searchKeyword
	 * @return ArrayList<Customer> object
	 */
	public List<Customer> getCustomers(String searchKeyword) {
		/*
		 * This method fetches one or more customers based on the searchKeyword and returns it as an ArrayList
		 */
		

		/*
		 * The students code to fetch data from the database based on searchKeyword will be written here
		 * Each record is required to be encapsulated as a "Customer" class object and added to the "customers" List
		 */
		
    	List<Customer> customers = new ArrayList<Customer>();
    	Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			// Loop through all Clients and add them to the List
			query = connection.prepareStatement("SELECT * FROM Client");
			results = query.executeQuery();
			while (results.next()) {
				System.out.println("found guy");
				Customer customer = getCustomer(results.getString("SSN"));
				if (customer != null && 
						(searchKeyword == null || (customer.getFirstName() + " " + customer.getLastName()).equals(searchKeyword) || customer.getLastName().contains(searchKeyword) || customer.getFirstName().contains(searchKeyword))) 
					customers.add(customer);
				else System.out.println(customer == null);
			}
			results.close();
			query.close();
			
			connection.close();
			
		} catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (connection != null) connection.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (connection != null) connection.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (query != null) query.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (results != null) results.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
			
		return customers;
	}


	public Customer getHighestRevenueCustomer() {
		/*
		 * This method fetches the customer who generated the highest total revenue and returns it
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			query = connection.prepareStatement(""
					+ "CREATE VIEW CustomerRevenue AS "
					+ "SELECT SUM(Transactions.Fee) AS Total, Trade.AccountId FROM Trade, Transactions "
					+ "WHERE Trade.TransactionId = Transactions.Id "
					+ "GROUP BY Trade.AccountId;"
					+ ""
					+ "SELECT Client.ID "
					+ "FROM CustomerRevenue AS z, Person, Client, Account "
					+ "WHERE Person.SSN = Client.SSN AND Client.ID = Account.ClientID AND z.AccountId = Account.ClientID AND z.Total = ("
					+ "SELECT MAX(x.Total) FROM CustomerRevenue AS x);"
					+ "DROP VIEW CustomerRevenue");
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			Customer customer = getCustomer(results.getString("ID"));
			results.close();
			query.close();
			
			connection.close();
			return customer;
			
		} catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (connection != null) connection.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (connection != null) connection.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (query != null) query.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (results != null) results.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
		
		return null;
	}

	public Customer getCustomer(String customerID) {

		/*
		 * This method fetches the customer details and returns it
		 * customerID, which is the Customer's ID who's details have to be fetched, is given as method parameter
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (customerID != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Get the Client with associated ID
				Customer customer = new Customer();
				customer.setClientId(customerID);
				query = connection.prepareStatement("SELECT * FROM Client WHERE ID = ?");
				query.setString(1, customerID);
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				customer.setSsn(results.getString("SSN"));
				customer.setRating(results.getInt("Rating"));
				customer.setCreditCard(results.getString("CreditCardNumber"));
				query.close();
				results.close();
				
				// Get the Account with associated ClientID
				query = connection.prepareStatement("SELECT * FROM Account WHERE ClientID = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				customer.setAccountNumber(results.getInt("AccountNumber"));
				customer.setAccountCreationTime("DateOpened");
				query.close();
				results.close();
				
				// Get the Person with associated SSN
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				customer.setId(results.getString("ID"));
				customer.setLastName(results.getString("LastName"));
				customer.setFirstName(results.getString("FirstName"));
				customer.setAddress(results.getString("Address"));
				customer.setTelephone(results.getString("Telephone"));
				customer.setEmail(results.getString("Email"));
				query.close();
				results.close();
				
				// Get the Location with associated ZipCode
				Location location = new Location();
				location.setZipCode(results.getInt("ZipCode"));
				query = connection.prepareStatement("SELECT * FROM Location WHERE ZipCode = ?");
				query.setInt(1, location.getZipCode());
				results = query.executeQuery();
				if (results.next()) {
					location.setCity(results.getString("City"));
					location.setState(results.getString("State"));
				}
				customer.setLocation(location);
				results.close();
				query.close();
				
				connection.close();
				return customer;
				
			} catch (SQLException e) {
	            System.out.println(e.getMessage());
	            try { if (connection != null) connection.rollback();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        } catch (Exception e) { System.out.println(e.getMessage());
	        } finally { // Close all open objects
	            try { if (connection != null) connection.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (query != null) query.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (results != null) results.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        }

		return null;
	}
	
	public String deleteCustomer(String customerID) {

		/*
		 * This method deletes a customer returns "success" string on success, else returns "failure"
		 * The students code to delete the data from the database will be written here
		 * customerID, which is the Customer's ID who's details have to be deleted, is given as method parameter
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (customerID != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Client
				query = connection.prepareStatement("SELECT ID FROM Client WHERE ID = ?");
				query.setString(1, customerID);
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Delete Client
				query = connection.prepareStatement("DELETE FROM Client WHERE ID = ?");
				query.setString(1, customerID);
				query.executeUpdate();
				query.close();
				
				connection.commit();
				connection.close();
				return "success";
				
			} catch (SQLException e) {
	            System.out.println(e.getMessage());
	            try { if (connection != null) connection.rollback();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        } catch (Exception e) { System.out.println(e.getMessage());
	        } finally { // Close all open objects
	            try { if (connection != null) connection.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (query != null) query.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (results != null) results.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        }
		
		return "failure";
		
	}


	public String getCustomerID(String email) {
		/*
		 * This method returns the Customer's ID based on the provided email address
		 * The students code to fetch data from the database will be written here
		 * username, which is the email address of the customer, who's ID has to be returned, is given as method parameter
		 * The Customer's ID is required to be returned as a String
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			// Get Person with matching Email
			query = connection.prepareStatement("SELECT SSN FROM Person WHERE Email = ?");
			query.setString(1, email);
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			query.close();
			results.close();
			
			// Get Client with matching SSN
			query = connection.prepareStatement("SELECT ID FROM Client WHERE SSN = ?");
			query.setString(1, results.getString("SSN"));
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			String id = results.getString("ID");
			query.close();
			results.close();
			
			connection.close();
			return id;
			
		} catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (connection != null) connection.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (connection != null) connection.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (query != null) query.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (results != null) results.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }

		return null;
	}


	public String addCustomer(Customer customer) {

		/*
		 * All the values of the add customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database insertion of the customer details and return "success" or "failure" based on result of the database insertion.
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (customer != null)
			try {
				System.out.println(String.format("ID: %s, SSN: %s, CustomerID: %s", customer.getId(), customer.getSsn(), customer.getClientId()));
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (results.next()) {
					System.out.println("daodaodaodaodaodaodao");
					throw new Exception();
				}
				query.close();
				results.close();
				
				// Check for existing Customer
				query = connection.prepareStatement("SELECT * FROM Client WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (results.next()) {
					System.out.println("i crave death");
					throw new Exception();
				}
				query.close();
				results.close();
				
				// Check for existing Account
				query = connection.prepareStatement("SELECT * FROM Account WHERE AccountNumber = ?");
				query.setInt(1, customer.getAccountNumber());
				results = query.executeQuery();
				if (results.next()) {
					System.out.println("Scott Smolka");
					throw new Exception();
				}
				query.close();
				results.close();
				
				// Check for existing location, add if not exists.
				query = connection.prepareStatement("SELECT ZipCode FROM Location WHERE ZipCode = ?");
				query.setInt(1, customer.getLocation().getZipCode());
				results = query.executeQuery();
				if (!results.next()) {  // Add location
					query = connection.prepareStatement("INSERT INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
					query.setInt(1, customer.getLocation().getZipCode());
					query.setString(2, customer.getLocation().getCity());
					query.setString(3, customer.getLocation().getState());
					query.executeUpdate();
				}
				query.close();
				results.close();
				
				// Add Person
				query = connection.prepareStatement("INSERT INTO Person(ID, SSN, LastName, FirstName, Address, ZipCode, Telephone, Email) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				query.setString(1, customer.getSsn());
				query.setString(2, customer.getSsn());
				query.setString(3, customer.getLastName());
				query.setString(4, customer.getFirstName());
				query.setString(5, customer.getAddress());
				query.setInt(6, customer.getLocation().getZipCode());
				query.setString(7, customer.getTelephone());
				query.setString(8, customer.getEmail());
				query.executeUpdate();
				query.close();
				
				// Add Client
				query = connection.prepareStatement("INSERT INTO Client(ID, SSN, Rating, CreditCardNumber) VALUES (?, ?, ?, ?)");
				query.setString(1, customer.getSsn());
				query.setString(2, customer.getSsn());
				query.setInt(3, customer.getRating());
				query.setString(4, customer.getCreditCard());
				query.executeUpdate();
				query.close();
				
				// Add Account
				query = connection.prepareStatement("INSERT INTO Account(AccountNumber, ClientID, DateOpened) VALUES (?, ?, ?)");
				query.setInt(1, customer.getAccountNumber());
				query.setString(2, customer.getSsn());
				query.setString(3, customer.getAccountCreationTime());
				query.executeUpdate();
				query.close();
				
				connection.commit();
				connection.close();
				return "success";
				
			} catch (SQLException e) {
	            System.out.println(e.getMessage());
	            try { if (connection != null) connection.rollback();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        } catch (Exception e) { System.out.println(e.getMessage());
	        } finally { // Close all open objects
	            try { if (connection != null) connection.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (query != null) query.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (results != null) results.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        }
		
		System.out.println("no.");
		return "failure";
	}

	public String editCustomer(Customer customer) {
		/*
		 * All the values of the edit customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (customer != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT SSN FROM Person WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing Customer
				query = connection.prepareStatement("SELECT * FROM Client WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing Account
				query = connection.prepareStatement("SELECT * FROM Account WHERE AccountNumber = ?");
				query.setInt(1, customer.getAccountNumber());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing location, add if not exists.
				query = connection.prepareStatement("SELECT ZipCode FROM Location WHERE ZipCode = ?");
				query.setInt(1, customer.getLocation().getZipCode());
				results = query.executeQuery();
				if (!results.next()) {  // Add location
					query = connection.prepareStatement("INSERT INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
					query.setInt(1, customer.getLocation().getZipCode());
					query.setString(2, customer.getLocation().getCity());
					query.setString(3, customer.getLocation().getState());
					query.executeUpdate();
				}
				query.close();
				results.close();
				
				// Update Person entry
				query = connection.prepareStatement("UPDATE Person "
						+ "SET ID = ?, LastName = ?, FirstName = ?, Address = ?, ZipCode = ?, Telephone = ?, Email = ? "
						+ "WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				query.setString(2, customer.getLastName());
				query.setString(3, customer.getFirstName());
				query.setString(4, customer.getAddress());
				query.setInt(5, customer.getLocation().getZipCode());
				query.setString(6, customer.getTelephone());
				query.setString(7, customer.getSsn());
				query.setString(8, customer.getEmail());
				query.executeUpdate();
				query.close();
				
				// Update Employee entry
				query = connection.prepareStatement("UPDATE Client "
						+ "SET ID = ?, Rating = ?, CreditCardNumber = ? "
						+ "WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				query.setInt(2, customer.getRating());
				query.setString(3, customer.getCreditCard());
				query.setString(4, customer.getSsn());
				query.executeUpdate();
				query.close();
				
				// Update Account entry
				query = connection.prepareStatement("UPDATE Account "
						+ "SET AccountNumber = ?, DateOpened = ? "
						+ "WHERE ClientID = ?");
				query.setInt(1, customer.getAccountNumber());
				query.setString(2, customer.getAccountCreationTime());
				query.setString(3, customer.getSsn());
				query.close();
				
				connection.commit();
				connection.close();
				return "success";
				
			} catch (SQLException e) {
	            System.out.println(e.getMessage());
	            try { if (connection != null) connection.rollback();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        } catch (Exception e) { System.out.println(e.getMessage());
	        } finally { // Close all open objects
	            try { if (connection != null) connection.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (query != null) query.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	            try { if (results != null) results.close();
	            } catch (Exception ee) { System.out.println(ee.getMessage()); }
	        }
			
		return "failure";

	}

    public List<Customer> getCustomerMailingList() {

		/*
		 * This method fetches the all customer mailing details and returns it
		 * The students code to fetch data from the database will be written here
		 */

        return getAllCustomers();
    }

    public List<Customer> getAllCustomers() {
        /*
		 * This method fetches returns all customers
		 */
    	
    	return getCustomers(null);
    }
}
