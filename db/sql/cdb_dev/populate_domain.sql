LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES
(1,'Location','Location Domain',1),
(2,'Catalog','Catalog Domain',2),
(3,'Inventory','Inventory Domain',3);
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;