CREATE TABLE Location (
	ZipCode INTEGER,
	City CHAR(20) NOT NULL,
	State CHAR(20) NOT NULL,
	PRIMARY KEY (ZipCode)
)

CREATE TABLE Person(
	SSN INTEGER,
	LastName CHAR(20) NOT NULL,
	FirstName CHAR(20) NOT NULL,
	Address CHAR(40),
	ZipCode INTEGER,
	Telephone BIGINT,
	PRIMARY KEY (SSN),
	FOREIGN KEY (ZipCode) REFERENCES Location (ZipCode)
		ON DELETE NO ACTION 
		ON UPDATE CASCADE
)

CREATE TABLE Employee (
	Id INTEGER IDENTITY(1,1),
	SSN INTEGER,
	StartDate DATE,
	HourlyRate INTEGER,
	PRIMARY KEY (Id),
	FOREIGN KEY (SSN) REFERENCES Person (SSN)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
)

CREATE TABLE Client (
	Email CHAR(128),
	Rating INTEGER,
	CreditCardNumber BIGINT,
	Id INTEGER CHECK (Id > 0 AND Id < 1000000000),
	PRIMARY KEY (Id),
	FOREIGN KEY (Id) REFERENCES Person (SSN)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
)

CREATE TABLE Account (
	Id INTEGER IDENTITY(1,1),
	DateOpened DATE,
	Client INTEGER CHECK (Client > 0 AND Client < 1000000000),
	PRIMARY KEY (Id),
	FOREIGN KEY (Client) REFERENCES Client(Id)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
)

CREATE TABLE Transactions (
	Id INTEGER IDENTITY(1,1),
	Fee DECIMAL(10,2),
	DateTime DATETIME,
	PricePerShare DECIMAL(10,2),
	PRIMARY KEY (Id)
)

CREATE TABLE Orders (
	NumShares INTEGER,
	PricePerShare DECIMAL(10,2),
	Id INTEGER IDENTITY(1,1),
	DateTime DATETIME,
	Percentage DECIMAL(5,2) CHECK (Percentage <= 100.00 AND Percentage >= 0.00),
	PriceType CHAR(20) CHECK (PriceType IN ('Market', 'MarketOnClose','TrailingStop','HiddenStop')),
	OrderType CHAR(5) CHECK (OrderType IN ('Buy','Sell')),
	PRIMARY KEY (Id)
)

CREATE TABLE Stock (
	StockSymbol CHAR(20) NOT NULL,
	CompanyName CHAR(20) NOT NULL,
	Type CHAR(20) NOT NULL,
	PricePerShare DECIMAL(10,2),
	PRIMARY KEY (StockSymbol)
)

CREATE TABLE Trade (
	AccountId INTEGER,
	BrokerId INTEGER,
	TransactionId INTEGER,
	OrderId INTEGER,
	StockId CHAR(20),
	PRIMARY KEY (AccountId,BrokerId,TransactionId,OrderId,StockId),
	FOREIGN KEY (AccountId) REFERENCES Account (Id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	FOREIGN KEY (BrokerId) REFERENCES Employee (Id)
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
)

CREATE TABLE HasStock (
	AccountId INTEGER,
	StockId CHAR(20),
	NumShares INTEGER
	PRIMARY KEY (AccountId, StockId),
	FOREIGN KEY (AccountId) REFERENCES Account (Id)
		ON DELETE NO ACTION
		ON UPDATE CASCADE,
	FOREIGN KEY (StockId) REFERENCES Stock (StockSymbol)
		ON DELETE NO ACTION
		ON UPDATE CASCADE
)