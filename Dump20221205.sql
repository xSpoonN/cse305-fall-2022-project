CREATE DATABASE  IF NOT EXISTS `jalleonardi` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `jalleonardi`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: mysql3.cs.stonybrook.edu    Database: jalleonardi
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Account`
--

DROP TABLE IF EXISTS `Account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Account` (
  `AccountNumber` int(11) NOT NULL,
  `ClientID` char(40) DEFAULT NULL,
  `DateOpened` char(60) DEFAULT NULL,
  PRIMARY KEY (`AccountNumber`),
  KEY `ClientID` (`ClientID`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`ClientID`) REFERENCES `Client` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Account`
--

LOCK TABLES `Account` WRITE;
/*!40000 ALTER TABLE `Account` DISABLE KEYS */;
INSERT INTO `Account` VALUES (0,'SSn2',NULL);
/*!40000 ALTER TABLE `Account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Client`
--

DROP TABLE IF EXISTS `Client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Client` (
  `ID` char(40) NOT NULL,
  `SSN` char(20) DEFAULT NULL,
  `Rating` int(11) DEFAULT NULL,
  `CreditCardNumber` char(40) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `SSN` (`SSN`),
  CONSTRAINT `client_ibfk_1` FOREIGN KEY (`SSN`) REFERENCES `Person` (`SSN`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Client`
--

LOCK TABLES `Client` WRITE;
/*!40000 ALTER TABLE `Client` DISABLE KEYS */;
INSERT INTO `Client` VALUES ('SSn2','SSn2',32,'CC2');
/*!40000 ALTER TABLE `Client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Employee`
--

DROP TABLE IF EXISTS `Employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee` (
  `ID` char(40) NOT NULL,
  `SSN` char(20) DEFAULT NULL,
  `StartDate` char(50) DEFAULT NULL,
  `HourlyRate` float DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `SSN` (`SSN`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`SSN`) REFERENCES `Person` (`SSN`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Employee`
--

LOCK TABLES `Employee` WRITE;
/*!40000 ALTER TABLE `Employee` DISABLE KEYS */;
/*!40000 ALTER TABLE `Employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HasStock`
--

DROP TABLE IF EXISTS `HasStock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HasStock` (
  `AccountId` int(11) NOT NULL,
  `StockId` char(20) NOT NULL,
  `NumShares` int(11) DEFAULT NULL,
  PRIMARY KEY (`AccountId`,`StockId`),
  KEY `StockId` (`StockId`),
  CONSTRAINT `hasstock_ibfk_1` FOREIGN KEY (`AccountId`) REFERENCES `Account` (`AccountNumber`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `hasstock_ibfk_2` FOREIGN KEY (`StockId`) REFERENCES `Stock` (`StockSymbol`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HasStock`
--

LOCK TABLES `HasStock` WRITE;
/*!40000 ALTER TABLE `HasStock` DISABLE KEYS */;
INSERT INTO `HasStock` VALUES (0,'AAPL',295),(0,'PPRD',5),(0,'WATR',422);
/*!40000 ALTER TABLE `HasStock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Location`
--

DROP TABLE IF EXISTS `Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Location` (
  `ZipCode` int(11) NOT NULL,
  `City` char(40) NOT NULL,
  `State` char(40) NOT NULL,
  PRIMARY KEY (`ZipCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (11111,'City','State'),(11112,'New City','New State');
/*!40000 ALTER TABLE `Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Login`
--

DROP TABLE IF EXISTS `Login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Login` (
  `Username` char(40) NOT NULL,
  `Password` char(40) DEFAULT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Login`
--

LOCK TABLES `Login` WRITE;
/*!40000 ALTER TABLE `Login` DISABLE KEYS */;
INSERT INTO `Login` VALUES ('cust@1','p'),('emp@1','p'),('mikey@mike','MTz012402');
/*!40000 ALTER TABLE `Login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `NumShares` int(11) DEFAULT NULL,
  `PricePerShare` decimal(10,2) DEFAULT NULL,
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `DateTime` datetime DEFAULT NULL,
  `Percentage` decimal(5,2) DEFAULT NULL,
  `PriceType` char(20) DEFAULT NULL,
  `OrderType` char(5) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES (99,160.00,18,'2022-12-04 17:34:15',0.00,'Market','Buy'),(89,160.00,19,'2022-12-04 17:42:31',0.00,'Market','Buy'),(8789678,160.00,20,'2022-12-04 17:53:55',0.00,'Market','Sell'),(8789678,160.00,21,'2022-12-04 17:54:10',0.00,'Market','Sell'),(9,160.00,22,'2022-12-04 17:55:43',0.00,'Market','Sell'),(1,160.00,23,'2022-12-05 12:27:36',0.00,'Market','Buy'),(20,160.00,24,'2022-12-05 12:28:10',0.00,'Market','Sell'),(1,160.00,25,'2022-12-05 12:41:45',0.00,'Market','Buy'),(1,160.00,26,'2022-12-05 12:41:45',0.00,'Market','Buy'),(343,140.00,27,'2022-12-05 12:48:29',0.00,'Market','Buy'),(22,140.00,28,'2022-12-05 12:49:05',0.00,'Market','Buy'),(232,120.00,29,'2022-12-05 12:49:59',0.00,'Market','Buy'),(232,120.00,30,'2022-12-05 12:50:09',0.00,'Market','Buy'),(232,120.00,31,'2022-12-05 12:50:23',0.00,'Market','Buy'),(3,120.00,32,'2022-12-05 12:50:26',0.00,'Market','Buy'),(33,120.00,33,'2022-12-05 12:50:30',0.00,'Market','Buy'),(3,120.00,34,'2022-12-05 12:54:33',0.00,'Market','Buy'),(88,120.00,35,'2022-12-05 12:57:47',0.00,'Market','Buy'),(8,3000.00,36,'2022-12-05 13:01:30',0.00,'Market','Buy'),(10,3000.00,37,'2022-12-05 13:01:47',0.00,'Market','Sell'),(300,3000.00,38,'2022-12-05 13:02:43',0.00,'Market','Buy'),(69,3000.00,39,'2022-12-05 13:18:41',0.00,'Market','Buy'),(45,3000.00,40,'2022-12-05 13:21:52',0.00,'Market','Buy'),(5,10.00,41,'2022-12-05 13:22:38',0.00,'Market','Buy'),(5,120.00,42,'2022-12-05 13:23:18',0.00,'Market','Buy'),(5,120.00,43,'2022-12-05 13:23:41',0.00,'Market','Buy'),(5,120.00,44,'2022-12-05 13:29:41',0.00,'Market','Buy'),(9,120.00,45,'2022-12-05 13:33:11',0.00,'Market','Buy');
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Person`
--

DROP TABLE IF EXISTS `Person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Person` (
  `ID` char(40) DEFAULT NULL,
  `SSN` char(20) NOT NULL,
  `LastName` char(40) NOT NULL,
  `FirstName` char(40) NOT NULL,
  `Address` char(50) DEFAULT NULL,
  `ZipCode` int(11) DEFAULT NULL,
  `Telephone` char(20) DEFAULT NULL,
  `Email` char(50) DEFAULT NULL,
  PRIMARY KEY (`SSN`),
  KEY `ZipCode` (`ZipCode`),
  CONSTRAINT `person_ibfk_1` FOREIGN KEY (`ZipCode`) REFERENCES `Location` (`ZipCode`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Person`
--

LOCK TABLES `Person` WRITE;
/*!40000 ALTER TABLE `Person` DISABLE KEYS */;
INSERT INTO `Person` VALUES ('SSN','SSN','one1','emp1','Address1',11112,'Number1','emp@1'),('SSn2','SSn2','Jadams2','Cecil2','Address2',11112,'Phone2','cust@1');
/*!40000 ALTER TABLE `Person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Stock`
--

DROP TABLE IF EXISTS `Stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Stock` (
  `StockSymbol` char(20) NOT NULL,
  `CompanyName` char(20) NOT NULL,
  `Type` char(20) NOT NULL,
  `PricePerShare` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`StockSymbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Stock`
--

LOCK TABLES `Stock` WRITE;
/*!40000 ALTER TABLE `Stock` DISABLE KEYS */;
INSERT INTO `Stock` VALUES ('AAPL','Apple','Technology',120.00),('GOOGL','Google','Technology',300.00),('PPRD','Pepperidge Farms','Agriculture',10.00),('WATR','Water','Water',3000.00);
/*!40000 ALTER TABLE `Stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Trade`
--

DROP TABLE IF EXISTS `Trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Trade` (
  `AccountId` int(11) NOT NULL,
  `BrokerId` char(40) NOT NULL,
  `TransactionId` int(11) NOT NULL,
  `OrderId` int(11) NOT NULL,
  `StockId` char(20) NOT NULL,
  PRIMARY KEY (`AccountId`,`BrokerId`,`TransactionId`,`OrderId`,`StockId`),
  KEY `BrokerId` (`BrokerId`),
  KEY `TransactionId` (`TransactionId`),
  KEY `OrderId` (`OrderId`),
  KEY `StockId` (`StockId`),
  CONSTRAINT `trade_ibfk_1` FOREIGN KEY (`AccountId`) REFERENCES `Account` (`AccountNumber`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `trade_ibfk_3` FOREIGN KEY (`TransactionId`) REFERENCES `Transactions` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `trade_ibfk_4` FOREIGN KEY (`OrderId`) REFERENCES `Orders` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `trade_ibfk_5` FOREIGN KEY (`StockId`) REFERENCES `Stock` (`StockSymbol`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Trade`
--

LOCK TABLES `Trade` WRITE;
/*!40000 ALTER TABLE `Trade` DISABLE KEYS */;
INSERT INTO `Trade` VALUES (0,'',18,18,'AAPL'),(0,'',19,19,'AAPL'),(0,'',22,22,'AAPL'),(0,'',23,23,'AAPL'),(0,'',24,24,'AAPL'),(0,'',25,25,'AAPL'),(0,'',28,28,'AAPL'),(0,'',35,35,'AAPL'),(0,'',36,36,'WATR'),(0,'',38,38,'WATR'),(0,'',39,39,'WATR'),(0,'',40,40,'WATR'),(0,'',41,41,'PPRD'),(0,'',42,42,'AAPL'),(0,'',43,43,'AAPL'),(0,'',44,44,'AAPL'),(0,'SSN',45,45,'AAPL');
/*!40000 ALTER TABLE `Trade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transactions`
--

DROP TABLE IF EXISTS `Transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transactions` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Fee` decimal(10,2) DEFAULT NULL,
  `DateTime` datetime DEFAULT NULL,
  `PricePerShare` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transactions`
--

LOCK TABLES `Transactions` WRITE;
/*!40000 ALTER TABLE `Transactions` DISABLE KEYS */;
INSERT INTO `Transactions` VALUES (18,792.00,'2022-12-04 17:34:15',160.00),(19,712.00,'2022-12-04 17:42:31',160.00),(20,70317424.00,'2022-12-04 17:53:55',160.00),(21,70317424.00,'2022-12-04 17:54:10',160.00),(22,72.00,'2022-12-04 17:55:43',160.00),(23,8.00,'2022-12-05 12:27:36',160.00),(24,160.00,'2022-12-05 12:28:10',160.00),(25,8.00,'2022-12-05 12:41:45',160.00),(26,8.00,'2022-12-05 12:41:45',160.00),(27,2401.00,'2022-12-05 12:48:29',140.00),(28,154.00,'2022-12-05 12:49:05',140.00),(29,1392.00,'2022-12-05 12:49:59',120.00),(30,1392.00,'2022-12-05 12:50:09',120.00),(31,1392.00,'2022-12-05 12:50:23',120.00),(32,18.00,'2022-12-05 12:50:26',120.00),(33,198.00,'2022-12-05 12:50:30',120.00),(34,18.00,'2022-12-05 12:54:33',120.00),(35,528.00,'2022-12-05 12:57:47',120.00),(36,1200.00,'2022-12-05 13:01:30',3000.00),(37,1500.00,'2022-12-05 13:01:47',3000.00),(38,45000.00,'2022-12-05 13:02:43',3000.00),(39,10350.00,'2022-12-05 13:18:41',3000.00),(40,6750.00,'2022-12-05 13:21:52',3000.00),(41,2.50,'2022-12-05 13:22:38',10.00),(42,30.00,'2022-12-05 13:23:18',120.00),(43,30.00,'2022-12-05 13:23:41',120.00),(44,30.00,'2022-12-05 13:29:41',120.00),(45,54.00,'2022-12-05 13:33:11',120.00);
/*!40000 ALTER TABLE `Transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `employeeearnings`
--

DROP TABLE IF EXISTS `employeeearnings`;
/*!50001 DROP VIEW IF EXISTS `employeeearnings`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `employeeearnings` AS SELECT 
 1 AS `Total`,
 1 AS `SSN`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `employeeearnings`
--

/*!50001 DROP VIEW IF EXISTS `employeeearnings`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`jalleonardi`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `employeeearnings` AS select sum(`transactions`.`Fee`) AS `Total`,`employee`.`SSN` AS `SSN` from ((`trade` join `transactions`) join `employee`) where ((`trade`.`BrokerId` = `employee`.`ID`) and (`trade`.`TransactionId` = `transactions`.`Id`)) group by `employee`.`SSN` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-05 14:37:00
