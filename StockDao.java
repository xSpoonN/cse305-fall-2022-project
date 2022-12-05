package dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Random;

import model.Stock;

public class StockDao {
    public List<Stock> getActivelyTradedStocks() {
		/* The students code to return list of actively traded stocks */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT StockId, COUNT(StockId) AS NumOrders FROM Trade GROUP BY StockId ORDER BY NumOrders DESC LIMIT 10");
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(getStockBySymbol(rs.getString("StockId")));
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

	public List<Stock> getAllStocks() {
		/* The students code to return list of stocks */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM Stock"); rs = ps.executeQuery();
            while (rs.next()) {
                out.add(getStockBySymbol(rs.getString("StockSymbol")));
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
	}

    public Stock getStockBySymbol(String stockSymbol) {
        /* The students code to return stock matching symbol */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; Stock out = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM Stock WHERE StockSymbol = ?"); 
			ps.setString(1, stockSymbol); rs = ps.executeQuery(); rs.next();
			out = new Stock(); Random rand = new Random();
			out.setNumShares(rand.nextInt(1000000000));
			out.setSymbol(rs.getString("StockSymbol")); out.setName(rs.getString("CompanyName"));
			out.setPrice(rs.getDouble("PricePerShare")); out.setType(rs.getString("Type"));
			conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

    public String setStockPrice(String stockSymbol, double stockPrice) {
        /* The students code to perform price update of the stock symbol */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            /* Check that the symbol exists */
            ps = conn.prepareStatement("SELECT StockSymbol FROM Stock WHERE StockSymbol = ?");
            ps.setString(1, stockSymbol); rs = ps.executeQuery();
            String symcheck = null;
            while (rs.next()) symcheck = rs.getString("StockSymbol");
            ps.close(); rs.close();
            if (symcheck == null) { conn.rollback(); ps.close(); rs.close(); conn.close(); return "failure"; }
			/* Update stock price */
			ps = conn.prepareStatement("UPDATE Stock SET PricePerShare = ? WHERE StockSymbol = ?");
			ps.setDouble(1,stockPrice); ps.setString(2, stockSymbol);
			ps.executeUpdate(); ps.close();
			conn.commit(); rs.close(); conn.close(); return "success";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return "failure";
    }
	
	public List<Stock> getOverallBestsellers() {
		/* The students code to get list of bestseller stocks */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT StockId, COUNT(StockId) AS NumOrders FROM Trade GROUP BY StockId ORDER BY NumOrders DESC LIMIT 5");
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(getStockBySymbol(rs.getString("StockId")));
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
	}

    public List<Stock> getCustomerBestsellers(String customerID) {
		/* The students code to get list of customer bestseller stocks */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement(
				"SELECT StockId, COUNT(StockId) AS NumOrders FROM Trade, Account, Client " +
				"WHERE Trade.AccountId = Account.AccountNumber AND Account.ClientID = Client.ID AND Client.ID = ? " +
				"GROUP BY StockId ORDER BY NumOrders DESC LIMIT 5");
			ps.setString(1, customerID); rs = ps.executeQuery();
            while (rs.next()) {
                out.add(getStockBySymbol(rs.getString("StockId")));
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

	public List<Stock> getStocksByCustomer(String customerId) {
		/* The students code to get stockHoldings of customer with customerId */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement(
				"SELECT Stock.*, HasStock.NumShares FROM HasStock,Stock,Account " + 
				"WHERE Account.ClientID = ? AND HasStock.AccountId = Account.AccountNumber AND HasStock.StockID = Stock.StockSymbol");
			ps.setString(1, customerId); rs = ps.executeQuery();
            while (rs.next()) {
				Stock stock = new Stock(); stock.setSymbol(rs.getString("StockSymbol"));
				stock.setName(rs.getString("CompanyName")); stock.setNumShares(rs.getInt("NumShares"));
				stock.setPrice(rs.getDouble("PricePerShare")); stock.setType(rs.getString("Type"));
                out.add(stock);
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
	}

    public List<Stock> getStocksByName(String name) {
		/* The students code to return list of stocks matching "name" */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM Stock WHERE CompanyName = ?");
			ps.setString(1, name); rs = ps.executeQuery();
            while (rs.next()) {
				Stock stock = new Stock(); 
				stock.setName(rs.getString("CompanyName")); stock.setSymbol(rs.getString("StockSymbol"));
				stock.setPrice(rs.getDouble("PricePerShare")); stock.setType(rs.getString("Type")); Random rand = new Random();
			    stock.setNumShares(rand.nextInt(1000000000));
                out.add(stock);
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

    public List<Stock> getStockSuggestions(String customerID) {
		/* The students code to return stock suggestions for given "customerId" */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("CREATE VIEW CustomerStockTypes AS " +
						"SELECT COUNT(Stock.Type) AS NumOrders, Stock.Type " +
						"FROM Trade,Orders,Account,Client,Person,Stock " +
						"WHERE Client.ID = ? AND Trade.StockId = Stock.StockSymbol " +
						"	AND Person.SSN = Client.ID AND Client.ID = Account.ClientID AND Account.AccountNumber = Trade.AccountId " +
                        "AND Trade.OrderId = Orders.Id " +
						"GROUP BY Stock.Type; ");
            ps.setString(1, customerID); ps.execute(); ps.close();
            ps = conn.prepareStatement(
						"SELECT CustomerStockTypes.Type INTO @TopStock FROM CustomerStockTypes " +
						"	WHERE CustomerStockTypes.NumOrders = ( " +
						"		SELECT MAX(CustomerStockTypes.NumOrders) FROM CustomerStockTypes ) LIMIT 3;");
            ps.execute(); ps.close();
            ps = conn.prepareStatement(
						"SELECT * " +
						"FROM Stock WHERE Stock.Type = @TopStock");
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(getStockBySymbol(rs.getString("StockSymbol")));
            }
            ps.close();
            ps = conn.prepareStatement("DROP VIEW CustomerStockTypes");
            ps.execute();
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

    public List<Stock> getStockPriceHistory(String stockSymbol) {
		/* The students code to return list of stock objects, showing price history */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement( "SELECT T.StockId, S.CompanyName, S.Type, Tr.PricePerShare, Tr.DateTime " + 
										"FROM Trade T, Transactions Tr, Stock S WHERE T.StockId = S.StockSymbol AND Tr.Id = T.TransactionId AND T.StockId = ?");
			ps.setString(1, stockSymbol); rs = ps.executeQuery();
            while (rs.next()) {
				Stock stock = new Stock();
				stock.setSymbol(stockSymbol); stock.setPrice(rs.getDouble("PricePerShare"));
                stock.setNumShares((int) rs.getDouble("PricePerShare"));
                stock.setName(rs.getString("CompanyName")); stock.setType(rs.getString("Type"));
                out.add(stock);
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }

    public List<String> getStockTypes() {
		/* The students code to populate types with stock types */
        List<String> types = new ArrayList<String>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
			ps = conn.prepareStatement("SELECT Type FROM Stock");
			rs = ps.executeQuery();
			while (rs.next()) {
				if (!types.contains(rs.getString("Type"))) types.add(rs.getString("Type"));
			}
			conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return types;
    }

    public List<Stock> getStockByType(String stockType) {
		/* The students code to return list of stocks of type "stockType" */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Stock> out = new ArrayList<Stock>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM Stock WHERE Type = ?");
			ps.setString(1, stockType); rs = ps.executeQuery();
            while (rs.next()) {
				Stock stock = new Stock();
				stock.setSymbol(rs.getString("StockSymbol")); stock.setPrice(rs.getDouble("PricePerShare"));
				stock.setName(rs.getString("CompanyName")); stock.setType(stockType); Random rand = new Random();
			    stock.setNumShares(rand.nextInt(1000000000));
                out.add(stock);
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        } catch (Exception e) { System.out.println(e.getMessage());
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return out;
    }
}
