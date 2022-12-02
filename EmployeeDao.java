package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Customer;
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
		
		if (employee != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection("jdbc:");
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT SSN FROM Person WHERE SSN = ?");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				results = query.executeQuery();
				if (results.next()) return "failure";
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT SSN FROM Employee WHERE SSN = ?");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				results = query.executeQuery();
				if (results.next()) return "failure";
				
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
				
				// Add Person
				query = connection.prepareStatement("INSERT INTO Person(SSN, LastName, FirstName, Address, ZipCode, Telephone) "
						+ "VALUES (?, ?, ?, ?, ?, ?)");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				query.setString(2, employee.getLastName());
				query.setString(3, employee.getFirstName());
				query.setString(4, employee.getAddress());
				query.setInt(5, employee.getLocation().getZipCode());
				query.setString(6, employee.getTelephone());
				query.executeUpdate();
				
				// Add Employee
				query = connection.prepareStatement("INSERT INTO Employee(SSN, StartDate, HourlyRate) VALUES (?, ?, ?)");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				query.setString(2, employee.getStartDate());
				query.setFloat(3, employee.getHourlyRate());
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

	public String editEmployee(Employee employee) {
		/*
		 * All the values of the edit employee form are encapsulated in the employee object.
		 * These can be accessed by getter methods (see Employee class in model package).
		 * e.g. firstName can be accessed by employee.getFirstName() method.
		 * The sample code returns "success" by default.
		 * You need to handle the database update and return "success" or "failure" based on result of the database update.
		 */
		
		if (employee != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection("jdbc:");
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Person
				query = connection.prepareStatement("SELECT SSN FROM Person WHERE SSN = ?");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT SSN FROM Employee WHERE SSN = ?");
				query.setInt(1, Integer.parseInt(employee.getSsn()));
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
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
				
				// Update Person entry
				query = connection.prepareStatement("UPDATE Person "
						+ "SET LastName = ?, FirstName = ?, Address = ?, ZipCode = ?, Telephone = ? "
						+ "WHERE SSN = ?");
				query.setString(1, employee.getLastName());
				query.setString(2, employee.getFirstName());
				query.setString(3, employee.getAddress());
				query.setInt(4, employee.getLocation().getZipCode());
				query.setString(5, employee.getTelephone());
				query.setInt(6, Integer.parseInt(employee.getSsn()));
				query.executeUpdate();
				
				// Update Employee entry
				query = connection.prepareStatement("UPDATE Employee "
						+ "SET StartDate = ?, HourlyRate = ? "
						+ "WHERE SSN = ?");
				query.setString(1, employee.getStartDate());
				query.setFloat(2, employee.getHourlyRate());
				query.setInt(3, Integer.parseInt(employee.getSsn()));
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

	public String deleteEmployee(String employeeID) {
		/*
		 * employeeID, which is the Employee's ID which has to be deleted, is given as method parameter
		 * The sample code returns "success" by default.
		 * You need to handle the database deletion and return "success" or "failure" based on result of the database deletion.
		 */
		
		if (employeeID != null)
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection("jdbc:");
				connection.setAutoCommit(false);
				PreparedStatement query;
				ResultSet results;
				
				// Check for existing Employee
				query = connection.prepareStatement("SELECT Id FROM Employee WHERE Id = ?");
				query.setInt(1, Integer.parseInt(employeeID));
				results = query.executeQuery();
				if (!results.next()) return "failure";
				
				// Delete Employee
				query = connection.prepareStatement("DELETE FROM Employee WHERE Id = ?");
				query.setInt(1, Integer.parseInt(employeeID));
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

	
	public List<Employee> getEmployees() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Query to return details about all the employees must be implemented
		 * Each record is required to be encapsulated as a "Employee" class object and added to the "employees" List
		 */

		List<Employee> employees = new ArrayList<Employee>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:");
			connection.setAutoCommit(false);
			PreparedStatement query;
			ResultSet results;
			
			// Loop through all Employees and add them to the List
			query = connection.prepareStatement("SELECT * FROM Employee");
			results = query.executeQuery();
			while (results.next()) {
				Employee employee = getEmployee(String.valueOf(results.getInt("SSN")));
				if (employee != null) employees.add(employee);
			}
			
			results.close();
			query.close();
			connection.close();
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return employees;
	}

	public Employee getEmployee(String employeeID) {

		/*
		 * The students code to fetch data from the database based on "employeeID" will be written here
		 * employeeID, which is the Employee's ID who's details have to be fetched, is given as method parameter
		 * The record is required to be encapsulated as a "Employee" class object
		 */

		return getDummyEmployee();
	}
	
	public Employee getHighestRevenueEmployee() {
		
		/*
		 * The students code to fetch employee data who generated the highest revenue will be written here
		 * The record is required to be encapsulated as a "Employee" class object
		 */
		
		return getDummyEmployee();
	}

	public String getEmployeeID(String username) {
		/*
		 * The students code to fetch data from the database based on "username" will be written here
		 * username, which is the Employee's email address who's Employee ID has to be fetched, is given as method parameter
		 * The Employee ID is required to be returned as a String
		 */

		return "111-11-1111";
	}

}
