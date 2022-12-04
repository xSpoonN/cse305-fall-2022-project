package dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.text.SimpleDateFormat;

import model.RevenueItem;

public class SalesDao {
    public List<RevenueItem> getSalesReport(String month, String year) {
		/* The students code to query to get sales report for a particular month and year */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<RevenueItem> out = new ArrayList<RevenueItem>();
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement(
                "SELECT T.DateTime, T.PricePerShare, Trade.AccountId, Trade.StockId, Orders.NumShares FROM Transaction T, Trade, Orders " +
                "WHERE MONTH(T.DateTime) = ? AND YEAR(T.DateTime) = ? AND Trade.Id = Orders.Id AND Trade.TransactionId = T.Id");
            ps.setInt(1,Integer.parseInt(month)); ps.setInt(2,Integer.parseInt(year)); rs = ps.executeQuery();
            while (rs.next()) {
                RevenueItem ri = new RevenueItem();
                ri.setDate(formatted.parse(rs.getString("DateTime"))); ri.setPricePerShare(rs.getDouble("PricePerShare"));
                ri.setAccountId(String.valueOf(rs.getInt("AccountId"))); ri.setStockSymbol(rs.getString("StockId"));
                ri.setNumShares(rs.getInt("NumShares")); ri.setAmount(ri.getPricePerShare()*ri.getNumShares());
                out.add(ri);
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

    public List<RevenueItem> getSummaryListing(String searchKeyword) {
		/* The students code to query to fetch details of summary listing of revenue generated by a particular stock,
		 * stock type or customer must be implemented. Store the revenue generated by an item in the amount attribute */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<RevenueItem> out = new ArrayList<RevenueItem>();
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(LoginDao.dmConn,LoginDao.dmUser,LoginDao.dmPass);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            /* Search keyword as stocksymbol */
            ps = conn.prepareStatement("SELECT StockSymbol FROM Stock WHERE StockSymbol = ?");
            ps.setString(1, searchKeyword); rs = ps.executeQuery();
            String symcheck = null;
            while (rs.next()) symcheck = rs.getString("StockSymbol");
            ps.close(); rs.close();
            if (symcheck != null) {
                ps = conn.prepareStatement(
                    "SELECT T.DateTime, T.PricePerShare, Trade.AccountId, Trade.StockId, Orders.NumShares FROM Transaction T, Trade, Orders " +
                    "WHERE Trade.StockId = ? AND Trade.Id = Orders.Id AND Trade.TransactionId = T.Id");
                ps.setString(1,searchKeyword); rs = ps.executeQuery();
                while (rs.next()) {
                    RevenueItem ri = new RevenueItem();
                    ri.setDate(formatted.parse(rs.getString("DateTime"))); ri.setPricePerShare(rs.getDouble("PricePerShare"));
                    ri.setAccountId(String.valueOf(rs.getInt("AccountId"))); ri.setStockSymbol(rs.getString("StockId"));
                    ri.setNumShares(rs.getInt("NumShares")); ri.setAmount(ri.getPricePerShare()*ri.getNumShares());
                    out.add(ri);
                }
                conn.commit(); ps.close(); rs.close(); conn.close(); return out;
            }
            /* Search keyword as stock type */
            ps = conn.prepareStatement("SELECT Type FROM Stock WHERE Type = ?");
            ps.setString(1, searchKeyword); rs = ps.executeQuery();
            String typecheck = null;
            while (rs.next()) typecheck = rs.getString("Type");
            ps.close(); rs.close();
            if (typecheck != null) {
                ps = conn.prepareStatement(
                    "SELECT T.DateTime, T.PricePerShare, Trade.AccountId, Trade.StockId, Orders.NumShares " +
                    "FROM Transaction T, Trade, Orders, Stock " +
                    "WHERE Stock.Type = ? AND Trade.Id = Orders.Id AND Trade.TransactionId = T.Id AND Stock.StockSymbol = Trade.StockId");
                ps.setString(1,searchKeyword); rs = ps.executeQuery();
                while (rs.next()) {
                    RevenueItem ri = new RevenueItem();
                    ri.setDate(formatted.parse(rs.getString("DateTime"))); ri.setPricePerShare(rs.getDouble("PricePerShare"));
                    ri.setAccountId(String.valueOf(rs.getInt("AccountId"))); ri.setStockSymbol(rs.getString("StockId"));
                    ri.setNumShares(rs.getInt("NumShares")); ri.setAmount(ri.getPricePerShare()*ri.getNumShares());
                    out.add(ri);
                }
                conn.commit(); ps.close(); rs.close(); conn.close(); return out;
            }
            /* Search keyword as customer name */
            ps = conn.prepareStatement("SELECT FirstName, LastName FROM Person WHERE FirstName = ? AND LastName = ?");
            ps.setString(1, searchKeyword.split(" ")[0]); ps.setString(2, searchKeyword.split(" ")[1]); rs = ps.executeQuery();
            String namecheck = null;
            while (rs.next()) namecheck = rs.getString("FirstName");
            ps.close(); rs.close();
            if (namecheck != null) {
                ps = conn.prepareStatement(
                    "SELECT T.DateTime, T.PricePerShare, Trade.AccountId, Trade.StockId, Orders.NumShares " +
                    "FROM Transaction T, Trade, Orders, Account, Client, Person " +
                    "WHERE Person.FirstName = ? AND Person.LastName = ? AND Person.SSN = Client.Id AND Client.Id = Account.Client AND " +
                    "Account.Id = Trade.AccountId AND Trade.Id = Orders.Id AND Trade.TransactionId = T.Id AND Stock.StockSymbol = Trade.StockId");
                ps.setString(1,searchKeyword.split(" ")[0]); ps.setString(2,searchKeyword.split(" ")[1]); rs = ps.executeQuery();
                while (rs.next()) {
                    RevenueItem ri = new RevenueItem();
                    ri.setDate(formatted.parse(rs.getString("DateTime"))); ri.setPricePerShare(rs.getDouble("PricePerShare"));
                    ri.setAccountId(String.valueOf(rs.getInt("AccountId"))); ri.setStockSymbol(rs.getString("StockId"));
                    ri.setNumShares(rs.getInt("NumShares")); ri.setAmount(ri.getPricePerShare()*ri.getNumShares());
                    out.add(ri);
                }
                conn.commit(); ps.close(); rs.close(); conn.close(); return out;
            }
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
