LOCK TABLES `item_category` WRITE;
/*!40000 ALTER TABLE `item_category` DISABLE KEYS */;
INSERT INTO `item_category` VALUES
(1,'Mechanical/Accelerator',NULL),
(2,'Diagnostics',NULL),
(3,'Controls/Instrumentation',NULL),
(4,'Housing',NULL),
(5,'Power Supplies',NULL),
(6,'Magnets',NULL),
(7,'RF',NULL),
(8,'Mechanical/Beamlines',NULL),
(9,'Mechanical/Insertion Devices',NULL),
(10,'Lattice Elements',NULL),
(11,'Other',NULL);
/*!40000 ALTER TABLE `item_category` ENABLE KEYS */;
UNLOCK TABLES;
