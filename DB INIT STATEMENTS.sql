-- This SHOULD be optional, but we're including it just
-- in case. The db dump should already contain instructions
-- to instantiate the database.

CREATE TABLE Location (
	ZipCode INTEGER,
	City CHAR(40) NOT NULL,
	State CHAR(40) NOT NULL,
	PRIMARY KEY (ZipCode)
);

CREATE TABLE Person(
	ID CHAR(40),
	SSN CHAR(20),
	LastName CHAR(40) NOT NULL,
	FirstName CHAR(40) NOT NULL,
	Address CHAR(50),
	ZipCode INTEGER,
	Telephone CHAR(20),
	Email CHAR(50),
	PRIMARY KEY (SSN),
	FOREIGN KEY (ZipCode) REFERENCES Location (ZipCode)
		ON DELETE NO ACTION 
		ON UPDATE CASCADE
);

CREATE TABLE Employee (
	ID CHAR(40),
	SSN CHAR(20),
	StartDate CHAR(50),
	HourlyRate FLOAT(10),
	PRIMARY KEY (ID),
	FOREIGN KEY (SSN) REFERENCES Person (SSN)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
);

CREATE TABLE Client (
	ID CHAR(40),
	SSN CHAR(20),
	Rating INTEGER,
	CreditCardNumber CHAR(40),
	PRIMARY KEY (ID),
	FOREIGN KEY (SSN) REFERENCES Person (SSN)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
);

CREATE TABLE Account (
	AccountNumber INTEGER,
	ClientID CHAR(40),
	DateOpened CHAR(60),
	PRIMARY KEY (AccountNumber),
	FOREIGN KEY (ClientID) REFERENCES Client(ID)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
);

CREATE TABLE Login (
	Username CHAR(40),
	Password CHAR(40),
	PRIMARY KEY (Username)
);

CREATE TABLE Transactions (
	Id INTEGER AUTO_INCREMENT,
	Fee DECIMAL(10,2),
	DateTime DATETIME,
	PricePerShare DECIMAL(10,2),
	PRIMARY KEY (Id)
);

CREATE TABLE Orders (
	NumShares INTEGER,
	PricePerShare DECIMAL(10,2),
	Id INTEGER AUTO_INCREMENT,
	DateTime DATETIME,
	Percentage DECIMAL(5,2) CHECK (Percentage <= 100.00 AND Percentage >= 0.00),
	PriceType CHAR(20) CHECK (PriceType IN ('Market', 'MarketOnClose','TrailingStop','HiddenStop')),
	OrderType CHAR(5) CHECK (OrderType IN ('Buy','Sell')),
	PRIMARY KEY (Id)
);

CREATE TABLE Stock (
	StockSymbol CHAR(20) NOT NULL,
	CompanyName CHAR(20) NOT NULL,
	Type CHAR(20) NOT NULL,
	PricePerShare DECIMAL(10,2),
	PRIMARY KEY (StockSymbol)
);

CREATE TABLE Trade (
	AccountId INTEGER,
	BrokerId CHAR(40),
	TransactionId INTEGER,
	OrderId INTEGER,
	StockId CHAR(20),
	PRIMARY KEY (AccountId,BrokerId,TransactionId,OrderId,StockId),
	FOREIGN KEY (AccountId) REFERENCES Account (AccountNumber)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	FOREIGN KEY (TransactionId) REFERENCES Transactions (Id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	FOREIGN KEY (OrderId) REFERENCES Orders (Id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	FOREIGN KEY (StockId) REFERENCES Stock (StockSymbol)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);

CREATE TABLE HasStock (
	AccountId INTEGER,
	StockId CHAR(20),
	NumShares INTEGER,
	PRIMARY KEY (AccountId, StockId),
	FOREIGN KEY (AccountId) REFERENCES Account (AccountNumber)
		ON DELETE NO ACTION
		ON UPDATE CASCADE,
	FOREIGN KEY (StockId) REFERENCES Stock (StockSymbol)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
);


---- If need be, uncomment these lines.
--DROP TABLE Account;
--DROP TABLE Client;
--DROP TABLE Employee;
--DROP TABLE HasStock;
--DROP TABLE Location;
--DROP TABLE Login;
--DROP TABLE Orders;
--DROP TABLE Person;
--DROP TABLE Stock;
--DROP TABLE Trade;
--DROP TABLE Transactions;

-- Add initial admin account - this will NOT have a Person associated with it.
-- Use it to create an Employee, then log in with that instead.
INSERT INTO Login VALUES ('admin@admin','admin');

-- Add Stocks according to this format.
INSERT INTO Stock VALUES ('AAPL', 'Apple', 'Technology', 150);
INSERT INTO Stock VALUES ('GOOGL', 'Google', 'Technology', 300);
INSERT INTO Stock VALUES ('PPRD', 'Pepperidge Farms', 'Agriculture', 10);
INSERT INTO Stock VALUES ('WATR', 'Water', 'Water', 3000);
