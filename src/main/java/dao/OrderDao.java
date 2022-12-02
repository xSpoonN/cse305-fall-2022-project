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

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPercentage(12.0);
        return order;
    }

    public Order getDummyMarketOrder() {
        MarketOrder order = new MarketOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyMarketOnCloseOrder() {
        MarketOnCloseOrder order = new MarketOnCloseOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyHiddenStopOrder() {
        HiddenStopOrder order = new HiddenStopOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPricePerShare(145.0);
        return order;
    }

    public List<Order> getDummyOrders() {
        List<Order> orders = new ArrayList<Order>();

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyTrailingStopOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOnCloseOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyHiddenStopOrder());
        }

        return orders;
    }

    public String submitOrder(Order order, Customer customer, Employee employee, Stock stock) {
		/* Student code to place stock order. Employee can be null, when the order is placed directly by Customer */
        Connection conn = null; Statement sm = null; PreparedStatement ps; ResultSet rs;
        Date date = new Date();  SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String time = formatted.format(date); String symbol = stock.getSymbol(); int accnum = customer.getAccountNumber();
        try {
            Class.forName(null); //Placeholder
            conn = DriverManager.getConnection(""); //Placeholder
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sm = conn.createStatement();
            int shares = order.getNumShares(); double price = stock.getPrice(); double fee = 0.05*shares*price;
            ps = conn.prepareStatement("INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?);");
            ps.setInt(1, shares); ps.setDouble(2,price); ps.setString(3, time); ps.setDouble(4,0.0);
            if (order instanceof MarketOrder || order instanceof MarketOnCloseOrder) {
                String priceType, orderType;
                //Adds to table Order
                if (order instanceof MarketOrder) { priceType = "Market"; orderType = ((MarketOrder)order).getBuySellType();
                } else { priceType = "MarketOnClose"; orderType = ((MarketOnCloseOrder)order).getBuySellType(); }
                ps.setString(5,priceType); ps.setString(6, orderType);
                ps.executeUpdate();
                //Adds to table Transaction
                ps = conn.prepareStatement("INSERT INTO Transaction VALUES (?, ?, ?);");
                ps.setDouble(1, fee); ps.setString(2,time); ps.setDouble(3,price);
                ps.executeUpdate();
                //Gets Id of just added Order and Transaction
                ps = conn.prepareStatement("SELECT * FROM Orders WHERE DateTime = ? AND NumShares = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setInt(2, shares); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int orderid = rs.getInt("Id");
                ps = conn.prepareStatement("SELECT * FROM Transactions WHERE DateTime = ? AND Fee = ? AND PricePerShare = ?");
                ps.setString(1, time); ps.setDouble(2, fee); ps.setDouble(3, price);
                rs = ps.executeQuery(); rs.next(); int transid = rs.getInt("Id");
                //Adds to table Trade
                ps = conn.prepareStatement("INSERT INTO Trade VALUES (?,?,?,?,?);");
                ps.setInt(1, accnum); ps.setInt(3, transid); ps.setInt(4, orderid); ps.setString(5, symbol);
                if (employee == null) ps.setNull(2, java.sql.Types.INTEGER);
                else ps.setInt(2, Integer.parseInt(employee.getEmployeeID()));
                ps.executeUpdate();
                //See if the entry exists in the account
                ps = conn.prepareStatement("SELECT COUNT(*) AS amount FROM HasStock WHERE StockId = ? AND AccountId = ?");
                ps.setString(1, symbol); ps.setInt(2, accnum);
                rs = ps.executeQuery(); rs.next(); int owned = rs.getInt("amount");
                //Gets the number of shares in this account
                ps = conn.prepareStatement("SELECT * FROM HasStock WHERE StockID = ? AND AccountId = ?");
                ps.setString(1, symbol); ps.setInt(2, accnum);
                rs = ps.executeQuery(); rs.next(); int amtowned = rs.getInt("NumShares");
                //Updates the customer's inventory
                ps = conn.prepareStatement("UPDATE HasStock SET NumShares = ? WHERE AccountId = ? AND StockId = ?");
                ps.setInt(2, accnum); ps.setString(3, symbol);
                if (orderType.equals("Sell")) {
                    if (owned == 1 && amtowned > shares) { ps.setInt(1, amtowned-shares); ps.executeUpdate();
                    } else { ps.close(); rs.close(); sm.close(); conn.close(); return "missinginventory"; }
                } else { //Buy type
                    if (owned == 1) { ps.setInt(1, amtowned+shares); ps.executeUpdate();
                    } else {
                        ps = conn.prepareStatement("INSERT INTO HasStock VALUES (?, ?, ?);");
                        ps.setInt(1, accnum); ps.setString(2, symbol); ps.setInt(3, shares);
                        ps.executeUpdate();
                    }
                }
                conn.commit(); ps.close(); rs.close(); sm.close(); conn.close();
            } else if (order instanceof HiddenStopOrder) {
                
            }

        } catch(Exception e) {}


        
		/*Sample data begins*/
        return "success";
		/*Sample data ends*/

    }

    public List<Order> getOrderByStockSymbol(String stockSymbol) {
        /*
		 * Student code to get orders by stock symbol
         */
        return getDummyOrders();
    }

    public List<Order> getOrderByCustomerName(String customerName) {
         /*
		 * Student code to get orders by customer name
         */
        return getDummyOrders();
    }

    public List<Order> getOrderHistory(String customerId) {
        /*
		 * The students code to fetch data from the database will be written here
		 * Show orders for given customerId
		 */
        return getDummyOrders();
    }


    public List<OrderPriceEntry> getOrderPriceHistory(String orderId) {

        /*
		 * The students code to fetch data from the database will be written here
		 * Query to view price history of hidden stop order or trailing stop order
		 * Use setPrice to show hidden-stop price and trailing-stop price
		 */
        List<OrderPriceEntry> orderPriceHistory = new ArrayList<OrderPriceEntry>();

        for (int i = 0; i < 10; i++) {
            OrderPriceEntry entry = new OrderPriceEntry();
            entry.setOrderId(orderId);
            entry.setDate(new Date());
            entry.setStockSymbol("aapl");
            entry.setPricePerShare(150.0);
            entry.setPrice(100.0);
            orderPriceHistory.add(entry);
        }
        return orderPriceHistory;
    }
}
