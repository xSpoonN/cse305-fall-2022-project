package dao;

import model.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDao {

    public Order getDummyTrailingStopOrder() {
        TrailingStopOrder order = new TrailingStopOrder();
        order.setId(1); order.setDatetime(new Date()); order.setNumShares(5); order.setPercentage(12.0); return order;
    }
    public Order getDummyMarketOrder() {
        MarketOrder order = new MarketOrder();
        order.setId(1); order.setDatetime(new Date()); order.setNumShares(5); order.setBuySellType("buy"); return order;
    }
    public Order getDummyMarketOnCloseOrder() {
        MarketOnCloseOrder order = new MarketOnCloseOrder();
        order.setId(1); order.setDatetime(new Date()); order.setNumShares(5); order.setBuySellType("buy"); return order;
    }
    public Order getDummyHiddenStopOrder() {
        HiddenStopOrder order = new HiddenStopOrder();
        order.setId(1); order.setDatetime(new Date()); order.setNumShares(5); order.setPricePerShare(145.0); return order;
    }
    public List<Order> getDummyOrders() {
        List<Order> orders = new ArrayList<Order>();
        for (int i = 0; i < 3; i++) { orders.add(getDummyTrailingStopOrder()); }
        for (int i = 0; i < 3; i++) { orders.add(getDummyMarketOrder()); }
        for (int i = 0; i < 3; i++) { orders.add(getDummyMarketOnCloseOrder()); }
        for (int i = 0; i < 3; i++) { orders.add(getDummyHiddenStopOrder()); }
        return orders;
    }

    public String submitOrder(Order order, Customer customer, Employee employee, Stock stock) {
		/* Student code to place stock order. Employee can be null, when the order is placed directly by Customer */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        Date date = new Date();  SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String time = formatted.format(date); String symbol = stock.getSymbol(); int accnum = customer.getAccountNumber();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            int shares = order.getNumShares(); double price = stock.getPrice(); double fee = 0.05*shares*price;
            if (order instanceof MarketOrder || order instanceof MarketOnCloseOrder) {
                String priceType, orderType;
                /* Adds to table Order */
                ps = conn.prepareStatement("INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?);");
                ps.setInt(1, shares); ps.setDouble(2,price); ps.setString(3, time); ps.setDouble(4,0.0);
                if (order instanceof MarketOrder) { priceType = "Market"; orderType = ((MarketOrder)order).getBuySellType();
                } else { priceType = "MarketOnClose"; orderType = ((MarketOnCloseOrder)order).getBuySellType(); }
                ps.setString(5,priceType); ps.setString(6, orderType);
                ps.executeUpdate(); ps.close();
                /* Adds to table Transaction */
                ps = conn.prepareStatement("INSERT INTO Transaction VALUES (?, ?, ?);");
                ps.setDouble(1, fee); ps.setString(2,time); ps.setDouble(3,price);
                ps.executeUpdate(); ps.close();
                /* Gets Id of just added Order and Transaction */
                ps = conn.prepareStatement("SELECT * FROM Orders WHERE DateTime = ? AND NumShares = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setInt(2, shares); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int orderid = rs.getInt("Id"); ps.close(); rs.close();
                ps = conn.prepareStatement("SELECT * FROM Transactions WHERE DateTime = ? AND Fee = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setDouble(2, fee); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int transid = rs.getInt("Id"); ps.close(); rs.close();
                /* Adds to table Trade */
                ps = conn.prepareStatement("INSERT INTO Trade VALUES (?,?,?,?,?);");
                ps.setInt(1, accnum); ps.setInt(3, transid); ps.setInt(4, orderid); ps.setString(5, symbol);
                if (employee == null) ps.setNull(2, java.sql.Types.INTEGER);
                else ps.setInt(2, Integer.parseInt(employee.getEmployeeID()));
                ps.executeUpdate(); ps.close();
                /* See if the entry exists in the account */
                ps = conn.prepareStatement("SELECT COUNT(*) AS amount FROM HasStock WHERE StockId = ? AND AccountId = ?");
                ps.setString(1, symbol); ps.setInt(2, accnum);
                rs = ps.executeQuery(); rs.next(); int owned = rs.getInt("amount"); ps.close(); rs.close();
                /* Gets the number of shares in this account */
                ps = conn.prepareStatement("SELECT * FROM HasStock WHERE StockID = ? AND AccountId = ?");
                ps.setString(1, symbol); ps.setInt(2, accnum);
                rs = ps.executeQuery(); rs.next(); int amtowned = rs.getInt("NumShares"); ps.close(); rs.close();
                /* Updates the customer's inventory */
                ps = conn.prepareStatement("UPDATE HasStock SET NumShares = ? WHERE AccountId = ? AND StockId = ?");
                ps.setInt(2, accnum); ps.setString(3, symbol);
                if (orderType.equals("Sell")) {
                    if (owned == 1 && amtowned > shares) { ps.setInt(1, amtowned-shares); ps.executeUpdate(); ps.close();
                    } else { ps.close(); rs.close(); conn.close(); return "missinginventory"; }
                } else { //Buy type
                    if (owned == 1) { ps.setInt(1, amtowned+shares); ps.executeUpdate(); ps.close();
                    } else {
                        ps = conn.prepareStatement("INSERT INTO HasStock VALUES (?, ?, ?);");
                        ps.setInt(1, accnum); ps.setString(2, symbol); ps.setInt(3, shares);
                        ps.executeUpdate(); ps.close();
                    }
                }
                conn.commit(); ps.close(); rs.close(); conn.close();
            } else if (order instanceof TrailingStopOrder || order instanceof HiddenStopOrder) {
                /* Check that the account has enough inventory */
                ps = conn.prepareStatement("SELECT * FROM HasStock WHERE AccountId = ? AND StockSymbol = ?"); 
                ps.setInt(1, accnum); ps.setString(2, symbol);
                rs = ps.executeQuery(); rs.next(); int amtowned = rs.getInt("NumShares"); ps.close(); rs.close();
                if (amtowned < shares) { ps.close(); rs.close(); conn.close(); return "missinginventory"; }
                /* Insert into Orders */
                ps = conn.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?,?);");
                ps.setInt(1, shares); ps.setString(3,time); ps.setString(6, "Sell");
                if (order instanceof TrailingStopOrder) {
                    ps.setNull(2, java.sql.Types.DECIMAL);
                    ps.setDouble(4, ((TrailingStopOrder)order).getPercentage()); ps.setString(5, "TrailingStop"); 
                } else { 
                    ps.setNull(4, java.sql.Types.DECIMAL);
                    ps.setDouble(2, ((HiddenStopOrder)order).getPricePerShare()); ps.setString(5, "HiddenStop"); 
                }
                ps.executeUpdate(); ps.close();
                /* Insert into Transactions */
                ps = conn.prepareStatement("INSERT INTO Transactions VALUES (?,?,?);");
                ps.setDouble(1,fee); ps.setString(2, time); ps.setDouble(3,price);
                ps.executeUpdate(); ps.close();
                /* Gets Id of just added Order and Transaction */
                ps = conn.prepareStatement("SELECT * FROM Orders WHERE DateTime = ? AND NumShares = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setInt(2, shares); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int orderid = rs.getInt("Id"); ps.close(); rs.close();
                ps = conn.prepareStatement("SELECT * FROM Transactions WHERE DateTime = ? AND Fee = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setDouble(2, fee); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int transid = rs.getInt("Id"); ps.close(); rs.close();
                /* Adds to table Trade */
                ps = conn.prepareStatement("INSERT INTO Trade VALUES (?,?,?,?,?);");
                ps.setInt(1, accnum); ps.setInt(3, transid); ps.setInt(4, orderid); ps.setString(5, symbol);
                if (employee == null) ps.setNull(2, java.sql.Types.INTEGER);
                else ps.setInt(2, Integer.parseInt(employee.getEmployeeID()));
                ps.executeUpdate(); ps.close();
                conn.commit(); rs.close(); conn.close();
            }
            return "success";
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback();
            } catch(Exception ee) { System.out.println(ee.getMessage()); }
        } catch(Exception e) { System.out.println(e.getMessage()); 
        } finally { // Close all open objects
            try { if (conn != null) conn.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (ps != null) ps.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
            try { if (rs != null) rs.close();
            } catch (Exception ee) { System.out.println(ee.getMessage()); }
        }
        return "fail";
    }

    public List<Order> getOrderByStockSymbol(String stockSymbol) {
        /* Student code to get orders by stock symbol */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Order> out = new ArrayList<Order>();
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            /* List all orders with this symbol */
            ps = conn.prepareStatement("SELECT Orders.* FROM Trade,Orders WHERE StockId = ? AND Trade.OrderId = Orders.Id");
            ps.setString(1,stockSymbol); rs = ps.executeQuery();
            while (rs.next()) {
                int shares = rs.getInt("NumShares"); int id = rs.getInt("Id"); double price = rs.getDouble("PricePerShare");
                Date date = formatted.parse(rs.getString("DateTime"));
                double percentage = rs.getDouble("Percentage");
                String priceType = rs.getString("PriceType"); String orderType = rs.getString("OrderType");
                Order order = new Order(); order.setDatetime(date); order.setId(id); order.setNumShares(shares);
                switch (priceType) {
                    case "Market":
                        ((MarketOrder)order).setBuySellType(orderType);
                        out.add((MarketOrder)order); break;
                    case "MarketOnClose":
                        ((MarketOnCloseOrder)order).setBuySellType(orderType);
                        out.add((MarketOnCloseOrder)order); break;
                    case "TrailingStop":
                        ((TrailingStopOrder)order).setPercentage(percentage);
                        out.add((TrailingStopOrder)order); break;
                    case "HiddenStop":
                        ((HiddenStopOrder)order).setPricePerShare(price);
                        out.add((HiddenStopOrder)order); break;
                }
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

    public List<Order> getOrderByCustomerName(String customerName) {
         /* Student code to get orders by customer name */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Order> out = new ArrayList<Order>();
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            /* List all orders with this name */
            ps = conn.prepareStatement(
                "SELECT Orders.* FROM Trade,Orders,Account,Client,Person" +
                "WHERE Person.LastName = ? AND Person.FirstName = ? AND" +
                "Person.SSN = Client.Id AND Client.Id = Account.Client AND Account.Id = Trade.AccountId AND Trade.OrderId = Orders.Id");
            ps.setString(1, customerName.split(" ")[1]); ps.setString(2, customerName.split(" ")[0]); rs = ps.executeQuery();
            while (rs.next()) {
                int shares = rs.getInt("NumShares"); int id = rs.getInt("Id"); double price = rs.getDouble("PricePerShare");
                Date date = formatted.parse(rs.getString("DateTime"));
                double percentage = rs.getDouble("Percentage");
                String priceType = rs.getString("PriceType"); String orderType = rs.getString("OrderType");
                Order order = new Order(); order.setDatetime(date); order.setId(id); order.setNumShares(shares);
                switch (priceType) {
                    case "Market":
                        ((MarketOrder)order).setBuySellType(orderType);
                        out.add((MarketOrder)order); break;
                    case "MarketOnClose":
                        ((MarketOnCloseOrder)order).setBuySellType(orderType);
                        out.add((MarketOnCloseOrder)order); break;
                    case "TrailingStop":
                        ((TrailingStopOrder)order).setPercentage(percentage);
                        out.add((TrailingStopOrder)order); break;
                    case "HiddenStop":
                        ((HiddenStopOrder)order).setPricePerShare(price);
                        out.add((HiddenStopOrder)order); break;
                }
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

    public List<Order> getOrderHistory(String customerId) {
        /* The students code to show orders for given customerId */
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Order> out = new ArrayList<Order>();
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            /* Get the customer's accountid */
            ps = conn.prepareStatement("SELECT Id FROM Account WHERE Client = ?");
            ps.setInt(1, Integer.parseInt(customerId));
            rs = ps.executeQuery(); rs.next(); int accid = rs.getInt("Id"); ps.close(); rs.close();
            ps = conn.prepareStatement(
                "SELECT O.DateTime, T.StockId, O.OrderType, O.NumShares, O.PricePerShare, O.PriceType, O.Percentage, T.BrokerId, T.TransactionId FROM Orders O, Trade T" +
                "WHERE O.Id = T.OrderId AND T.AccountId = ?");
            ps.setInt(1, accid); rs = ps.executeQuery();
            while (rs.next()) {
                int shares = rs.getInt("NumShares"); int id = rs.getInt("Id"); double price = rs.getDouble("PricePerShare");
                Date date = formatted.parse(rs.getString("DateTime"));
                double percentage = rs.getDouble("Percentage");
                String priceType = rs.getString("PriceType"); String orderType = rs.getString("OrderType");
                Order order = new Order(); order.setDatetime(date); order.setId(id); order.setNumShares(shares);
                switch (priceType) {
                    case "Market":
                        ((MarketOrder)order).setBuySellType(orderType);
                        out.add((MarketOrder)order); break;
                    case "MarketOnClose":
                        ((MarketOnCloseOrder)order).setBuySellType(orderType);
                        out.add((MarketOnCloseOrder)order); break;
                    case "TrailingStop":
                        ((TrailingStopOrder)order).setPercentage(percentage);
                        out.add((TrailingStopOrder)order); break;
                    case "HiddenStop":
                        ((HiddenStopOrder)order).setPricePerShare(price);
                        out.add((HiddenStopOrder)order); break;
                }
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


    public List<OrderPriceEntry> getOrderPriceHistory(String orderId) {
        /* The students code to query to view price history of hidden stop order or trailing stop order */
        List<OrderPriceEntry> orderPriceHistory = new ArrayList<OrderPriceEntry>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); conn.setAutoCommit(false);
            ps = conn.prepareStatement(
                "SELECT T.StockId, Tr.PricePerShare, Tr.DateTime, Orders.* FROM Trade T, Transactions Tr, Orders" +
                "WHERE T.OrderId = ? AND Orders.Id = T.OrderId");
            ps.setInt(1, Integer.parseInt(orderId)); rs = ps.executeQuery();
            while (rs.next()) {
                OrderPriceEntry order = new OrderPriceEntry(); 
                order.setDate(formatted.parse(rs.getString("DateTime"))); order.setOrderId(orderId); 
                order.setPricePerShare(rs.getDouble("PricePerShare")); order.setStockSymbol(rs.getString("StockSymbol"));
                orderPriceHistory.add(order);
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
        return orderPriceHistory;
    }
}
