package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Employee;
import model.Location;

public class EmployeeDao {
	/*
	 * This class handles all the database operations related to the employee table
	 */

    public Employee getDummyEmployee()
    {
        Employee employee = new Employee();

        Location location = new Location();
        location.setCity("Stony Brook");
        location.setState("NY");
        location.setZipCode(11790);

		/*Sample data begins*/
        employee.setEmail("shiyong@cs.sunysb.edu");
        employee.setFirstName("Shiyong");
        employee.setLastName("Lu");
        employee.setLocation(location);
        employee.setAddress("123 Success Street");
        employee.setStartDate("2006-10-17");
        employee.setTelephone("5166328959");
        employee.setEmployeeID("631-413-5555");
        employee.setHourlyRate(100);
		/*Sample data ends*/

        return employee;
    }

    public List<Employee> getDummyEmployees()
    {
       List<Employee> employees = new ArrayList<Employee>();

        for(int i = 0; i < 10; i++)
        {
            employees.add(getDummyEmployee());
        }

        return employees;
    }

	public String addEmployee(Employee employee) {

		/*
		 * All the values of the add employee form are encapsulated in the employee object.
		 * These can be accessed by getter methods (see Employee class in model package).
		 * e.g. firstName can be accessed by employee.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database insertion of the employee details and return "success" or "failure" based on result of the database insertion.
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (employee != null)
			try {
				System.out.println(String.format("ID: %s, SSN: %s, EmployeeID: %s", employee.getId(), employee.getSsn(), employee.getEmployeeID()));
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				results = query.executeQuery();
				if (results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT * FROM Employee WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				results = query.executeQuery();
				if (results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing location, add if not exists.
				query = connection.prepareStatement("SELECT * FROM Location WHERE ZipCode = ?");
				query.setInt(1, employee.getLocation().getZipCode());
				results = query.executeQuery();
				if (!results.next()) {  // Add location
					query = connection.prepareStatement("INSERT INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
					query.setInt(1, employee.getLocation().getZipCode());
					query.setString(2, employee.getLocation().getCity());
					query.setString(3, employee.getLocation().getState());
					query.executeUpdate();
				}
				query.close();
				results.close();
				
				// Add Person
				query = connection.prepareStatement("INSERT INTO Person(ID, SSN, LastName, FirstName, Address, ZipCode, Telephone, Email) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				query.setString(1, employee.getSsn());
				query.setString(2, employee.getSsn());
				query.setString(3, employee.getLastName());
				query.setString(4, employee.getFirstName());
				query.setString(5, employee.getAddress());
				query.setInt(6, employee.getLocation().getZipCode());
				query.setString(7, employee.getTelephone());
				query.setString(8, employee.getEmail());
				query.executeUpdate();
				query.close();
				
				// Add Employee
				query = connection.prepareStatement("INSERT INTO Employee(ID, SSN, StartDate, HourlyRate) VALUES (?, ?, ?, ?)");
				query.setString(1, employee.getSsn());
				query.setString(2, employee.getSsn());
				query.setString(3, employee.getStartDate());
				query.setFloat(4, employee.getHourlyRate());
				query.executeUpdate();
				
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

	public String editEmployee(Employee employee) {
		/*
		 * All the values of the edit employee form are encapsulated in the employee object.
		 * These can be accessed by getter methods (see Employee class in model package).
		 * e.g. firstName can be accessed by employee.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (employee != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT * FROM Employee WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Check for existing location, add if not exists.
				query = connection.prepareStatement("SELECT ZipCode FROM Location WHERE ZipCode = ?");
				query.setInt(1, employee.getLocation().getZipCode());
				results = query.executeQuery();
				if (!results.next()) {  // Add location
					query = connection.prepareStatement("INSERT INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
					query.setInt(1, employee.getLocation().getZipCode());
					query.setString(2, employee.getLocation().getCity());
					query.setString(3, employee.getLocation().getState());
					query.executeUpdate();
				}
				query.close();
				results.close();
				
				// Update Person entry
				query = connection.prepareStatement("UPDATE Person "
						+ "SET ID = ?, LastName = ?, FirstName = ?, Address = ?, ZipCode = ?, Telephone = ?, Email = ? "
						+ "WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				query.setString(2, employee.getLastName());
				query.setString(3, employee.getFirstName());
				query.setString(4, employee.getAddress());
				query.setInt(5, employee.getLocation().getZipCode());
				query.setString(6, employee.getTelephone());
				query.setString(7, employee.getSsn());
				query.setString(8, employee.getEmail());
				query.executeUpdate();
				query.close();
				
				// Update Employee entry
				query = connection.prepareStatement("UPDATE Employee "
						+ "SET ID = ?, StartDate = ?, HourlyRate = ? "
						+ "WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				query.setString(2, employee.getStartDate());
				query.setFloat(3, employee.getHourlyRate());
				query.setString(4, employee.getSsn());
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

	public String deleteEmployee(String employeeID) {
		/*
		 * employeeID, which is the Employee's ID which has to be deleted, is given as method parameter
		 * The sample code returns "success" by default.
		 * You need to handle the database deletion and return "success" or "failure" based on result of the database deletion.
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (employeeID != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT * FROM Employee WHERE ID = ?");
				query.setString(1, employeeID);
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				query.close();
				results.close();
				
				// Delete Employee
				query = connection.prepareStatement("DELETE FROM Employee WHERE ID = ?");
				query.setString(1, employeeID);
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

	
	public List<Employee> getEmployees() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Query to return details about all the employees must be implemented
		 * Each record is required to be encapsulated as a "Employee" class object and added to the "employees" List
		 */

		List<Employee> employees = new ArrayList<Employee>();
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			// Loop through all Employees and add them to the List
			query = connection.prepareStatement("SELECT * FROM Employee");
			results = query.executeQuery();
			while (results.next()) {
				Employee employee = getEmployee(results.getString("ID"));
				if (employee != null) employees.add(employee);
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
		
		return employees;
	}

	public Employee getEmployee(String employeeID) {

		/*
		 * The students code to fetch data from the database based on "employeeID" will be written here
		 * employeeID, which is the Employee's ID who's details have to be fetched, is given as method parameter
		 * The record is required to be encapsulated as a "Employee" class object
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		if (employeeID != null)
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
				connection.setAutoCommit(false);
				
				// Get the Employee with associated ID
				Employee employee = new Employee();
				employee.setEmployeeID(employeeID);
				query = connection.prepareStatement("SELECT * FROM Employee WHERE ID = ?");
				query.setString(1, employeeID);
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				employee.setSsn(results.getString("SSN"));
				employee.setStartDate(results.getString("StartDate"));
				employee.setHourlyRate(results.getFloat("HourlyRate"));
				query.close();
				results.close();
				
				// Get the Person with associated SSN
				query = connection.prepareStatement("SELECT * FROM Person WHERE SSN = ?");
				query.setString(1, employee.getSsn());
				results = query.executeQuery();
				if (!results.next()) throw new Exception();
				employee.setId(results.getString("ID"));
				employee.setLastName(results.getString("LastName"));
				employee.setFirstName(results.getString("FirstName"));
				employee.setAddress(results.getString("Address"));
				employee.setTelephone(results.getString("Telephone"));
				employee.setEmail(results.getString("Email"));
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
				employee.setLocation(location);
				query.close();
				results.close();
				
				connection.close();
				return employee;
				
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
	
	public Employee getHighestRevenueEmployee() {
		
		/*
		 * The students code to fetch employee data who generated the highest revenue will be written here
		 * The record is required to be encapsulated as a "Employee" class object
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			query = connection.prepareStatement(""
					+ "CREATE VIEW EmployeeEarnings AS "
					+ "SELECT SUM(Transactions.Fee) AS Total, Employee.SSN FROM Trade, Transactions, Employee "
					+ "WHERE Trade.BrokerId = Employee.Id AND Trade.TransactionId = Transactions.Id "
					+ "GROUP BY Employee.SSN;"
					+ ""
					+ "SELECT Employee.ID "
					+ "FROM EmployeeEarnings AS z, Person, Employee "
					+ "WHERE Person.SSN = Employee.SSN AND Employee.SSN = z.SSN AND z.Total = ("
					+ "SELECT MAX(x.Total) FROM EmployeeEarnings AS x);"
					+ "DROP VIEW EmployeeEarnings");
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			Employee employee = getEmployee(results.getString("ID"));
			results.close();
			query.close();
			
			connection.close();
			return employee;
			
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

	public String getEmployeeID(String username) {
		/*
		 * The students code to fetch data from the database based on "username" will be written here
		 * username, which is the Employee's email address who's Employee ID has to be fetched, is given as method parameter
		 * The Employee ID is required to be returned as a String
		 */
		Connection connection = null; PreparedStatement query = null; ResultSet results = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(LoginDao.dmConn, LoginDao.dmUser, LoginDao.dmPass);
			connection.setAutoCommit(false);
			
			// Get Person with matching Email
			query = connection.prepareStatement("SELECT SSN FROM Person WHERE Email = ?");
			query.setString(1, username);
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			String ssn = results.getString("SSN");
			query.close();
			results.close();
			
			// Get Employee with matching SSN
			query = connection.prepareStatement("SELECT ID FROM Employee WHERE SSN = ?");
			query.setString(1, ssn);
			results = query.executeQuery();
			if (!results.next()) throw new Exception();
			String id = results.getString("ID");
			results.close();
			query.close();
			
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

}
