CREATE TABLE PhysicalLocation (
	address CHAR(30),
	PRIMARY KEY ( address ) );
CREATE TABLE Sender (
	sender_id CHAR(4),
	name CHAR(20),
	address CHAR(30),
	PRIMARY KEY ( sender_id ),
  FOREIGN KEY ( address )
    REFERENCES PhysicalLocation (address)
    ON DELETE SET NULL );
CREATE TABLE Receiver (
	receiver_id CHAR(4),
	name CHAR(20),
	address CHAR(30),
	PRIMARY KEY ( receiver_id ),
  FOREIGN KEY ( address )
    REFERENCES PhysicalLocation (address)
    ON DELETE SET NULL );
CREATE TABLE PackagePricing (
	weight INTEGER NOT NULL,
	packagesize INTEGER NOT NULL,
	price INTEGER,
	PRIMARY KEY ( weight, packagesize ) );
CREATE TABLE Duration (
	senderaddress CHAR(30),
	receiveraddress CHAR(30),
	estimated_duration_of_delivery INTEGER NOT NULL,
	PRIMARY KEY (senderaddress, receiveraddress),
    FOREIGN KEY ( senderaddress )
		REFERENCES PhysicalLocation ( address )
		ON DELETE CASCADE,
    FOREIGN KEY ( receiveraddress )
		REFERENCES PhysicalLocation ( address )
		ON DELETE CASCADE );
CREATE TABLE Vehicle (
	vehicle_id CHAR(4),
	vehicle_location CHAR(30),
	PRIMARY KEY ( vehicle_id ),
  FOREIGN KEY (vehicle_location)
    REFERENCES PhysicalLocation (address)
    ON DELETE SET NULL );
CREATE TABLE DeliveryWorker (
	employee_id CHAR(4),
	name CHAR(20),
	collection_location CHAR(30),
	PRIMARY KEY ( employee_id ),
	FOREIGN KEY ( collection_location )
	  REFERENCES PhysicalLocation ( address )
    ON DELETE SET NULL );
CREATE TABLE Drives (
	vehicle_id CHAR(4),
	employee_id CHAR(4),
	PRIMARY KEY (vehicle_id, employee_id),
	FOREIGN KEY (vehicle_id)
		REFERENCES Vehicle (vehicle_id)
		ON DELETE CASCADE,
	FOREIGN KEY (employee_id)
		REFERENCES DeliveryWorker (employee_id)
		ON DELETE CASCADE );
CREATE TABLE TrackingInformation (
	tracking_id CHAR(4),
	package_dispatch_time TIMESTAMP,
	senderaddress CHAR(30),
	receiveraddress CHAR(30),
  estimated_arrival_of_delivery DATE,
  PRIMARY KEY (tracking_id),
	FOREIGN KEY ( senderaddress,  receiveraddress )
		REFERENCES Duration ( senderaddress,  receiveraddress )
		ON DELETE CASCADE );
CREATE TABLE Package (
  package_id CHAR(4),
	weight INTEGER NOT NULL,
	packagesize INTEGER NOT NULL,
	deliveryaddress CHAR(30) NOT NULL,
	returnaddress CHAR(30) NOT NULL,
	deliveredfrom CHAR(30) NOT NULL,
	trackinginfo CHAR(4) UNIQUE,
	PRIMARY KEY ( package_id ),
  FOREIGN KEY ( deliveredfrom )
    REFERENCES PhysicalLocation ( address )
    ON DELETE SET NULL,
  FOREIGN KEY ( deliveryaddress )
    REFERENCES PhysicalLocation ( address )
    ON DELETE SET NULL,
  FOREIGN KEY ( returnaddress )
    REFERENCES PhysicalLocation ( address )
    ON DELETE SET NULL,
  FOREIGN KEY (weight, packagesize)
	  REFERENCES PackagePricing (weight, packagesize)
	  ON DELETE SET NULL,
	FOREIGN KEY ( trackinginfo )
	  REFERENCES TrackingInformation ( tracking_id )
    ON DELETE SET NULL );
CREATE TABLE StoreLocationWorker (
	employee_id CHAR(4),
	name CHAR(20),
	storelocationaddress CHAR(30),
	PRIMARY KEY ( employee_id ),
	FOREIGN KEY ( storelocationaddress )
	  REFERENCES PhysicalLocation ( address )
		ON DELETE CASCADE );
INSERT INTO PhysicalLocation VALUES ('406 3rd Ave');
INSERT INTO PhysicalLocation VALUES ('123 Maple St');
INSERT INTO PhysicalLocation VALUES ('5432 Birch St');
INSERT INTO PhysicalLocation VALUES ('211 10th Ave');
INSERT INTO PhysicalLocation VALUES ('808 Cambie St');
INSERT INTO PhysicalLocation VALUES ('908 West Boulevard');
INSERT INTO PhysicalLocation VALUES ('123 Birch St');
INSERT INTO PhysicalLocation VALUES ('321 Main St');
INSERT INTO PhysicalLocation VALUES ('412 West Broadway');
INSERT INTO PhysicalLocation VALUES ('9187 Cambie St');
INSERT INTO Sender VALUES ('1234', 'Mary', '123 Birch St');
INSERT INTO Sender VALUES ('4321', 'Joe', '321 Main St');
INSERT INTO Sender VALUES ('2345', 'Andy', '412 West Broadway');
INSERT INTO Sender VALUES ('4354', 'Jim', '9187 Cambie St');
INSERT INTO Sender VALUES ('4677', 'Helen', '908 West Boulevard');
INSERT INTO Receiver VALUES ('1234', 'Helen', '908 West Boulevard');
INSERT INTO Receiver VALUES ('4321', 'Mary', '123 Birch St');
INSERT INTO Receiver VALUES ('2345', 'Joe', '321 Main St');
INSERT INTO Receiver VALUES ('4354', 'Andy', '412 West Broadway');
INSERT INTO Receiver VALUES ('4677', 'Jim', '9187 Cambie St');
INSERT INTO PackagePricing VALUES (5, 5, 5);
INSERT INTO PackagePricing VALUES (6, 6, 6);
INSERT INTO PackagePricing VALUES (7, 7, 7);
INSERT INTO PackagePricing VALUES (8, 9, 8);
INSERT INTO PackagePricing VALUES (10, 6, 9);
INSERT INTO Duration VALUES ('123 Birch St', '908 West Boulevard', 11);
INSERT INTO Duration VALUES ('321 Main St', '123 Birch St', 2);
INSERT INTO Duration VALUES ('412 West Broadway', '321 Main St', 5);
INSERT INTO Duration VALUES ('9187 Cambie St', '412 West Broadway', 7);
INSERT INTO Duration VALUES ('908 West Boulevard', '9187 Cambie St', 60);
INSERT INTO Vehicle VALUES ('0000', '123 Maple St');
INSERT INTO Vehicle VALUES ('0001', '406 3rd Ave');
INSERT INTO Vehicle VALUES ('0002', '5432 Birch St');
INSERT INTO Vehicle VALUES ('0003', '211 10th Ave');
INSERT INTO Vehicle VALUES ('0004', '808 Cambie St');
INSERT INTO DeliveryWorker VALUES ('0000', 'Bob', '123 Maple St');
INSERT INTO DeliveryWorker VALUES ('0001', 'Greg', '406 3rd Ave');
INSERT INTO DeliveryWorker VALUES ('0002', 'Paul', '5432 Birch St');
INSERT INTO DeliveryWorker VALUES ('0003', 'Lisa', '211 10th Ave');
INSERT INTO DeliveryWorker VALUES ('0004', 'Sam', '808 Cambie St');
INSERT INTO Drives VALUES ('0000','0000');
INSERT INTO Drives VALUES ('0000','0001');
INSERT INTO Drives VALUES ('0001','0000');
INSERT INTO Drives VALUES ('0002','0002');
INSERT INTO Drives VALUES ('0003','0003');
INSERT INTO Drives VALUES ('0004','0004');
INSERT INTO TrackingInformation VALUES ('6789', TIMESTAMP '2020-03-01 15:00:00.00', '123 Birch St', '908 West Boulevard', DATE '2020-03-10');
INSERT INTO TrackingInformation VALUES ('6798', TIMESTAMP '2020-02-20 18:00:00.00', '321 Main St', '123 Birch St', DATE '2020-03-01');
INSERT INTO TrackingInformation VALUES ('6978', TIMESTAMP '2020-02-25 12:00:00.00', '412 West Broadway', '321 Main St', DATE '2020-03-04');
INSERT INTO TrackingInformation VALUES ('9678', TIMESTAMP '2020-03-01 06:00:00.00', '9187 Cambie St', '412 West Broadway', DATE '2020-03-06');
INSERT INTO TrackingInformation VALUES ('5678', TIMESTAMP '2020-04-14 19:00:00.00', '908 West Boulevard', '9187 Cambie St', DATE '2020-04-28');
INSERT INTO Package VALUES ('4321', 5, 5, '908 West Boulevard', '123 Birch St', '123 Maple St', '6789');
INSERT INTO Package VALUES ('4312', 6, 6, '123 Birch St', '321 Main St', '406 3rd Ave', '6798');
INSERT INTO Package VALUES ('4132', 7, 7, '321 Main St', '412 West Broadway', '5432 Birch St', '6978');
INSERT INTO Package VALUES ('1432', 8, 9, '412 West Broadway', '9187 Cambie St', '211 10th Ave', '9678');
INSERT INTO Package VALUES ('5432', 10, 6, '9187 Cambie St', '908 West Boulevard', '808 Cambie St', '5678');
INSERT INTO StoreLocationWorker VALUES ('0005', 'Arthur', '123 Maple St');
INSERT INTO StoreLocationWorker VALUES ('0006', 'Sarah', '406 3rd Ave');
INSERT INTO StoreLocationWorker VALUES ('0007', 'Connor', '5432 Birch St');
INSERT INTO StoreLocationWorker VALUES ('0008', 'Mark', '211 10th Ave');
INSERT INTO StoreLocationWorker VALUES ('0009', 'Alice', '808 Cambie St');