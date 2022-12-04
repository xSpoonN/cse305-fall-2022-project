package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Customer;
import model.Location;

import java.util.stream.IntStream;

public class CustomerDao {
	/*
	 * This class handles all the database operations related to the customer table
	 */
	
	private String dmConn = "jdbc:mysql://localhost:3306/";

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
		
		return getDummyCustomerList();
	}


	public Customer getHighestRevenueCustomer() {
		/*
		 * This method fetches the customer who generated the highest total revenue and returns it
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */

		return getDummyCustomer();
	}

	public Customer getCustomer(String customerID) {

		/*
		 * This method fetches the customer details and returns it
		 * customerID, which is the Customer's ID who's details have to be fetched, is given as method parameter
		 * The students code to fetch data from the database will be written here
		 * The customer record is required to be encapsulated as a "Customer" class object
		 */
		
		return getDummyCustomer();
	}
	
	public String deleteCustomer(String customerID) {

		/*
		 * This method deletes a customer returns "success" string on success, else returns "failure"
		 * The students code to delete the data from the database will be written here
		 * customerID, which is the Customer's ID who's details have to be deleted, is given as method parameter
		 */

		if (customerID != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(dmConn);
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT ID FROM Client WHERE ID = ?");
				query.setString(1, customerID);
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
				// Delete Employee
				query = connection.prepareStatement("DELETE FROM Client WHERE Id = ?");
				query.setString(1, customerID);
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


	public String getCustomerID(String email) {
		/*
		 * This method returns the Customer's ID based on the provided email address
		 * The students code to fetch data from the database will be written here
		 * username, which is the email address of the customer, who's ID has to be returned, is given as method parameter
		 * The Customer's ID is required to be returned as a String
		 */

		return "111-11-1111";
	}


	public String addCustomer(Customer customer) {

		/*
		 * All the values of the add customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database insertion of the customer details and return "success" or "failure" based on result of the database insertion.
		 */
		if (customer != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(dmConn);
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (results.next()) return "failure";
				
				// Check for existing Customer
				query = connection.prepareStatement("SELECT * FROM Client WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (results.next()) return "failure";
				
				// Check for existing Account
				query = connection.prepareStatement("SELECT * FROM Account WHERE AccountNumber = ?");
				query.setInt(1, customer.getAccountNumber());
				results = query.executeQuery();
				if (results.next()) return "failure";
				
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
				
				// Add Person
				query = connection.prepareStatement("INSERT INTO Person(ID, SSN, LastName, FirstName, Address, ZipCode, Telephone) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
				query.setString(1, customer.getId());
				query.setString(2, customer.getSsn());
				query.setString(3, customer.getLastName());
				query.setString(4, customer.getFirstName());
				query.setString(5, customer.getAddress());
				query.setInt(6, customer.getLocation().getZipCode());
				query.setString(7, customer.getTelephone());
				query.executeUpdate();
				
				// Add Client
				query = connection.prepareStatement("INSERT INTO Client(ID, SSN, Rating, CreditCardNumber) VALUES (?, ?, ?, ?)");
				query.setString(1, customer.getClientId());
				query.setString(2, customer.getSsn());
				query.setInt(3, customer.getRating());
				query.setString(4, customer.getCreditCard());
				query.executeUpdate();
				
				// Add Account
				query = connection.prepareStatement("INSERT INTO Account(AccountNumber, ClientID, DateOpened) VALUES (?, ?, ?)");
				query.setInt(1, customer.getAccountNumber());
				query.setString(1, customer.getClientId());
				query.setString(2, customer.getAccountCreationTime());
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

	public String editCustomer(Customer customer) {
		/*
		 * All the values of the edit customer form are encapsulated in the customer object.
		 * These can be accessed by getter methods (see Customer class in model package).
		 * e.g. firstName can be accessed by customer.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		
		if (customer != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(dmConn);
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT SSN FROM Person WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
				// Check for existing Customer
				query = connection.prepareStatement("SELECT * FROM Client WHERE SSN = ?");
				query.setString(1, customer.getSsn());
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
				// Check for existing Account
				query = connection.prepareStatement("SELECT * FROM Account WHERE AccountNumber = ?");
				query.setInt(1, customer.getAccountNumber());
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
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
				
				// Update Person entry
				query = connection.prepareStatement("UPDATE Person "
						+ "SET ID = ?, LastName = ?, FirstName = ?, Address = ?, ZipCode = ?, Telephone = ?, Email = ? "
						+ "WHERE SSN = ?");
				query.setString(1, customer.getId());
				query.setString(2, customer.getLastName());
				query.setString(3, customer.getFirstName());
				query.setString(4, customer.getAddress());
				query.setInt(5, customer.getLocation().getZipCode());
				query.setString(6, customer.getTelephone());
				query.setString(7, customer.getSsn());
				query.setString(8, customer.getEmail());
				query.executeUpdate();
				
				// Update Employee entry
				query = connection.prepareStatement("UPDATE Client "
						+ "SET ID = ?, Rating = ?, CreditCardNumber = ? "
						+ "WHERE SSN = ?");
				query.setString(1, customer.getClientId());
				query.setInt(2, customer.getRating());
				query.setString(3, customer.getCreditCard());
				query.setString(4, customer.getSsn());
				query.executeUpdate();
				
				// Update Account entry
				query = connection.prepareStatement("UPDATE Account "
						+ "SET AccountNumber = ?, DateOpened = ? "
						+ "WHERE ClientID = ?");
				query.setInt(1, customer.getAccountNumber());
				query.setString(2, customer.getAccountCreationTime());
				query.setString(3, customer.getClientId());
				
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

    public List<Customer> getCustomerMailingList() {

		/*
		 * This method fetches the all customer mailing details and returns it
		 * The students code to fetch data from the database will be written here
		 */

        return getDummyCustomerList();
    }

    public List<Customer> getAllCustomers() {
        /*
		 * This method fetches returns all customers
		 */
        return getDummyCustomerList();
    }
}
