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
	FOREIGN KEY (BrokerId) REFERENCES Employee (SSN)
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

INSERT INTO Login VALUES ('jason@huh.com', 'asdf');

INSERT INTO Stock VALUES ('AAPL', 'Apple', 'Technology', 3.55);

SELECT * FROM Client;