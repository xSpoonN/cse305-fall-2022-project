SET XACT_ABORT ON -- Automatically abort the transaction on error.

--------------------------------------------------------------
-- Record an order
INSERT INTO Orders
VALUES (?, ?, ?, ?, ?, ?); 
--NumShares (INTEGER), PricePerShare (DECIMAL), DateTime (DATETIME), Percentage (DECIMAL), PriceType (STRING), OrderType (STRING)

--------------------------------------------------------------
-- Add/Edit/Delete information for a customer
-- Add
-- Add a new customer
BEGIN TRY BEGIN TRANSACTION;
INSERT INTO Person
VALUES (?, ?, ?, ?, ?, ?);
-- SSN (INTEGER), LastName (STRING), FirstName (STRING), Address (STRING), ZipCode (INTEGER), Telephone (INTEGER)
INSERT INTO Location
VALUES (?, ?, ?);
-- ZipCode (INTEGER), City (STRING), State (STRING)
INSERT INTO Client
VALUES (?, ?, ?, ?);
-- Email (STRING), Rating (INTEGER), CreditCardNumber (BIGINT), Id (INTEGER)
COMMIT TRANSACTION END TRY BEGIN CATCH BEGIN PRINT 'Person already exists' ROLLBACK END END CATCH

-- Edit
-- Edit an attribute from a table related to this customer
BEGIN TRY BEGIN TRANSACTION;
UPDATE ? -- Person/Client
SET ? = ? -- Attribute, Variable (STRING/INTEGER/etc.)
WHERE ? = ? -- SSN/ID, SSN (INTEGER)
COMMIT TRANSACTION END TRY BEGIN CATCH BEGIN PRINT 'Cannot find person' ROLLBACK END END CATCH

-- Delete
-- Delete an existing customer
BEGIN TRY BEGIN TRANSACTION;
DELETE FROM Person
WHERE SSN = ? -- SSN (INTEGER)
COMMIT TRANSACTION END TRY BEGIN CATCH BEGIN PRINT 'Person does not exist' ROLLBACK END END CATCH

-------------------------------------------------------------
-- Produce customer mailing lists
SELECT P.LastName, P.FirstName, C.Email
FROM Person P, Client C
WHERE P.SSN = C.Id

-------------------------------------------------------------
-- Produce a list of stock suggestions for a given customer
--    (based on that customer's past orders)
-- Select stock data from types that this customer has purchased in the past
GO
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