SET XACT_ABORT ON -- Automatically abort the transaction on error.

-------------------------------------------------------
-- A customer's current stock holdings
SELECT A.Id, S.StockSymbol, S.CompanyName, H.NumShares
FROM Stock S, HasStock H, Account A
WHERE A.Id = ? -- SSN (INTEGER)

-------------------------------------------------------
-- Share-price and trailing-stop history for a given conditional order
SELECT T.StockId, Tr.PricePerShare, Tr.DateTime, Orders.Percentage
FROM Trade T, Transactions Tr, Orders
WHERE T.StockId = ? AND Orders.Id = T.OrderId AND Orders.PriceType = 'TrailingStop'
-- StockSymbol (STRING)

-------------------------------------------------------
-- Share-price and hidden-stop history for a given conditional order
SELECT T.StockId, Tr.PricePerShare, Tr.DateTime, Orders.PricePerShare
FROM Trade T, Transactions Tr, Orders
WHERE T.StockId = ? AND Orders.Id = T.OrderId AND Orders.PriceType = 'HiddenStop'
-- StockSymbol (STRING)

-------------------------------------------------------
-- Share-price history of a given stock over a period of time
SELECT T.StockId, Tr.PricePerShare, Tr.DateTime
FROM Trade T, Transactions Tr
WHERE T.StockId = ? AND Tr.DateTime > ?
-- StockId and Tr.DateTime are compared against variables (STRING, DATETIME)

-------------------------------------------------------
-- A history of all current and past orders a customer has placed
SELECT O.DateTime, T.StockId, O.OrderType, O.NumShares, O.PricePerShare, O.PriceType, O.Percentage, T.BrokerId, T.TransactionId
FROM Orders O, Trade T
WHERE O.Id = T.OrderId AND T.AccountId = ? -- ID (INTEGER)

-------------------------------------------------------
-- Stock available of a given type and most-recent order info
SELECT S.Type, S.StockSymbol, S.CompanyName, S.PricePerShare, O.DateTime, O.NumShares, O.PricePerShare, O.OrderType
FROM Trade T, Stock S, Orders O
WHERE S.Type = ? AND S.StockSymbol = T.StockId AND O.Id = T.OrderId
-- S.Type compared against a variable (STRING)


-------------------------------------------------------
-- Stocks available with a particular keyword or set of keywords in the stock name,
-- and most-recent order info
SELECT S.StockSymbol, S.CompanyName, S.PricePerShare, O.DateTime, O.NumShares, O.PricePerShare, O.OrderType
FROM Trade T, Stock S, Orders O
WHERE S.CompanyName LIKE '%keyword%' AND S.StockSymbol = T.StockId AND O.Id = T.OrderId
-- S.CompanyName compared against a variable, this will be managed by the program

-------------------------------------------------------
-- Best-Seller list of stocks
SELECT TOP 5 StockId, COUNT(StockId) AS NumOrders
FROM Trade
GROUP BY StockId
ORDER BY NumOrders DESC

-------------------------------------------------------
-- Personalized Stock suggestion List
GO --Same as Broker 4
CREATE VIEW CustomerStockTypes AS --Table of the amount of times a type shows up in a customer's order history
SELECT COUNT(Stock.Type) AS NumOrders, Stock.Type
FROM Trade,Orders,Account,Client,Person,Stock --The orders placed by a specific customer
WHERE Person.LastName = ? AND Person.FirstName = ? AND Trade.StockId = Stock.StockSymbol --LastName (STRING), FirstName (STRING)
	AND Person.SSN = Client.Id AND Client.Id = Account.Client AND Account.Id = Trade.AccountId AND Trade.OrderId = Orders.Id
GROUP BY Stock.Type
GO
DECLARE @TopStock CHAR(20) --Declare a new variable
SET @TopStock = ( --Select the most bought stock type.
	SELECT TOP 1 CustomerStockTypes.Type FROM CustomerStockTypes
	WHERE CustomerStockTypes.NumOrders = (
		SELECT MAX(CustomerStockTypes.NumOrders) FROM CustomerStockTypes ))
SELECT * --Select all stocks that match that type
FROM Stock WHERE Stock.Type = @TopStock
DROP VIEW CustomerStockTypes